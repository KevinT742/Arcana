package arcana;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import arcana.mc.ArcanaBlocks;
import arcana.mc.ArcanaItems;

/**
 * Arcana: the magic of the ground, made usable.
 *
 * <p>Arcana is a member of the mod family. It does not invent a magic of its own: the free
 * floating magic of every region comes from Genesis, and Arcana only gives the player ways to
 * work with it — an altar that draws it out of the ground or pours it back in, and a lens that
 * shows how much of it there is.
 *
 * <p>Because the magic of a world is stored, what an altar does to a region stays done: the
 * ground of a region that was drained stays poor until somebody fills it again.
 */
@Mod(ArcanaMod.MODID)
public class ArcanaMod {

    /** The unique identifier of the mod. */
    public static final String MODID = "arcana";

    /** The registry that holds the creative tab of the mod. */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    /** The creative tab of the mod. */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB =
            CREATIVE_MODE_TABS.register("arcana", ArcanaMod::tab);

    public ArcanaMod(IEventBus modEventBus, ModContainer modContainer) {
        ArcanaBlocks.BLOCKS.register(modEventBus);
        ArcanaItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    /** The creative tab of the mod. */
    private static CreativeModeTab tab() {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 8)
                .title(Component.translatable("itemGroup.arcana"))
                .icon(() -> ArcanaItems.LENS_ITEM.get().getDefaultInstance())
                .build();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // The materials of Arcana join the registry of Genesis, so that a block of Arcana has
        // a material just like a block of Genesis.
        ArcanaBlocks.registerProfiles();
        ArcanaItems.registerProfiles();
    }
}
