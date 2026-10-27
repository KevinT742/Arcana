package arcana.mc;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import arcana.api.RitualResult;
import arcana.core.ritual.RitualRules;

import genesis.api.magic.MagicConcentration;
import genesis.api.material.MaterialProfile;
import genesis.mc.GenesisApi;

/**
 * The altar: the place where the magic of the ground is drawn out or poured back in.
 *
 * <p>Three things can be done with it:
 * <ul>
 *   <li>an empty bottle draws the magic of the ground into the bottle,</li>
 *   <li>a bottle that already carries magic pours it back into the ground,</li>
 *   <li>any magic material can be sacrificed, and its magic becomes the magic of the ground.</li>
 * </ul>
 *
 * <p>Whatever the altar does to the ground stays done: the magic of a world is stored, so the
 * ground of a region that was drained stays poor until somebody fills it again.
 */
public class ArcaneAltar extends Block {

    public ArcaneAltar(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel) level;
        Optional<MagicConcentration> concentration = ArcanaRituals.concentration(server, pos);
        if (concentration.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcana.message.not_genesis"));
            return InteractionResult.FAIL;
        }
        MagicConcentration magic = concentration.get();
        player.sendSystemMessage(Component.translatable("arcana.message.concentration",
                String.format("%.3f", magic.value()), magic.tier()));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel server = (ServerLevel) level;

        // A bottle: empty it draws the magic of the ground, full it pours the magic back.
        if (stack.is(ArcanaItems.VIAL_ITEM.get())) {
            if (ArcaneVial.isFull(stack)) {
                pourBack(server, pos, player, stack);
            } else {
                drawOut(server, pos, player, stack);
            }
            return InteractionResult.SUCCESS;
        }

        // Any magic material may be sacrificed.
        Optional<MaterialProfile> material = GenesisApi.materialOf(stack.getItem());
        if (material.isPresent() && material.get().isMagicMaterial()) {
            sacrifice(server, pos, player, stack, material.get());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    /** Draws the magic of the ground into the bottle. */
    private void drawOut(ServerLevel server, BlockPos pos, Player player, ItemStack bottle) {
        Optional<RitualResult> result = ArcanaRituals.drain(server, pos);
        if (result.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcana.message.not_genesis"));
            return;
        }
        RitualResult ritual = result.get();
        switch (ritual.outcome()) {
            case COLLECTED -> {
                ArcaneVial.setCharge(bottle, RitualRules.fillOf(ritual.collected()));
                player.sendSystemMessage(Component.translatable("arcana.message.collected",
                        String.format("%.3f", ritual.collected())));
            }
            case TOO_FEW -> player.sendSystemMessage(Component.translatable("arcana.message.too_few"));
            default -> player.sendSystemMessage(Component.translatable("arcana.message.nothing_happened"));
        }
    }

    /** Pours the magic of the bottle back into the ground. */
    private void pourBack(ServerLevel server, BlockPos pos, Player player, ItemStack bottle) {
        double offering = ArcaneVial.chargeOf(bottle) * RitualRules.CAPACITY;
        Optional<RitualResult> result = ArcanaRituals.infuse(server, pos, offering);
        if (result.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcana.message.not_genesis"));
            return;
        }
        RitualResult ritual = result.get();
        switch (ritual.outcome()) {
            case OFFERED -> {
                ArcaneVial.setCharge(bottle, 0.0);
                player.sendSystemMessage(Component.translatable("arcana.message.offered",
                        String.format("%.3f", ritual.added())));
            }
            case SATURATED -> player.sendSystemMessage(Component.translatable("arcana.message.saturated"));
            default -> player.sendSystemMessage(Component.translatable("arcana.message.nothing_happened"));
        }
    }

    /** Sacrifices a magic material, and its magic becomes the magic of the ground. */
    private void sacrifice(ServerLevel server, BlockPos pos, Player player, ItemStack stack,
            MaterialProfile material) {
        double offering = material.solidifiedMagic();
        Optional<RitualResult> result = ArcanaRituals.infuse(server, pos, offering);
        if (result.isEmpty()) {
            player.sendSystemMessage(Component.translatable("arcana.message.not_genesis"));
            return;
        }
        RitualResult ritual = result.get();
        switch (ritual.outcome()) {
            case OFFERED -> {
                stack.consume(1, player);
                player.sendSystemMessage(Component.translatable("arcana.message.offered",
                        String.format("%.3f", ritual.added())));
            }
            case SATURATED -> player.sendSystemMessage(Component.translatable("arcana.message.saturated"));
            case NOT_MAGIC -> player.sendSystemMessage(Component.translatable("arcana.message.not_magic"));
            default -> player.sendSystemMessage(Component.translatable("arcana.message.nothing_happened"));
        }
    }
}
