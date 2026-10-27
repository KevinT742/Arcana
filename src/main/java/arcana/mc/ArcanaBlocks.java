package arcana.mc;

import arcana.ArcanaMod;

import genesis.api.material.BlockTemplate;
import genesis.api.material.MaterialFamily;
import genesis.api.material.MaterialRegistry;
import genesis.mc.GenesisApi;

import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The blocks of Arcana.
 *
 * <p>The altar is made of the same arcane material as the arcane brick of Genesis, and it behaves
 * the way that material says it should: its hardness, its blast resistance and its sound are all
 * derived from the profile, not written down here.
 */
public final class ArcanaBlocks {

    /** The registry that holds the blocks of the mod. */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ArcanaMod.MODID);

    /** The altar: the place where the magic of the ground is drawn out or poured back in. */
    public static final BlockTemplate ALTAR = BlockTemplate.builder("arcane_altar")
            .profile(MaterialFamily.arcane("arcana:arcane_altar"))
            .toolTier(BlockTemplate.ToolTier.DIAMOND)
            .sound(BlockTemplate.SoundKind.STONE)
            .lightLevel(2)
            .build();

    public static final DeferredBlock<ArcaneAltar> ALTAR_BLOCK =
            BLOCKS.registerBlock("arcane_altar", properties -> new ArcaneAltar(
                    GenesisApi.blockProperties(ALTAR).apply(properties)));

    public static final DeferredItem<BlockItem> ALTAR_ITEM =
            ArcanaItems.ITEMS.registerSimpleBlockItem("arcane_altar", ALTAR_BLOCK);

    private ArcanaBlocks() {
    }

    /** Registers the material of the altar with the registry of Genesis. */
    public static void registerProfiles() {
        MaterialRegistry.register(ALTAR.profile());
    }
}
