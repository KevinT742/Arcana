package arcana.mc;

import java.util.function.Consumer;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * A bottle that can carry magic.
 *
 * <p>What the bottle carries is stored on the stack itself, so two bottles in the same chest can
 * hold different amounts. The magic of a bottle is not a property of the item but of the thing
 * that is in it.
 */
public class ArcaneVial extends Item {

    /** The key the amount of magic is stored under. */
    private static final String CHARGE = "charge";

    public ArcaneVial(Properties properties) {
        super(properties);
    }

    /** How much magic the bottle carries, between {@code 0} and {@code 1}. */
    public static double chargeOf(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0.0;
        }
        return data.copyTag().getDoubleOr(CHARGE, 0.0);
    }

    /** Puts a new amount of magic into the bottle. */
    public static void setCharge(ItemStack stack, double charge) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putDouble(CHARGE, charge));
    }

    /** Whether the bottle carries any magic at all. */
    public static boolean isFull(ItemStack stack) {
        return chargeOf(stack) > 0.0;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        double charge = chargeOf(stack);
        if (charge > 0.0) {
            tooltip.accept(Component.translatable("arcana.tooltip.vial_charge",
                    String.format("%.0f%%", charge * 100.0)));
        } else {
            tooltip.accept(Component.translatable("arcana.tooltip.vial_empty"));
        }
    }
}
