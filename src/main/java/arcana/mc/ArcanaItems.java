package arcana.mc;

import arcana.ArcanaMod;

import genesis.api.item.ItemCategory;
import genesis.api.item.ItemRarity;
import genesis.api.item.ItemTemplate;
import genesis.api.material.MaterialFamily;
import genesis.api.material.MaterialRegistry;
import genesis.mc.GenesisApi;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The items of Arcana.
 *
 * <p>Both of them are described with the material template of Genesis, so what they are made of —
 * and therefore what they weigh and how they behave — is decided by the same rules as everything
 * else in the world.
 */
public final class ArcanaItems {

    /** The registry that holds the items of the mod. */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ArcanaMod.MODID);

    /** A lens of crystal: it lets you see the magic of the ground. */
    public static final ItemTemplate LENS = ItemTemplate.builder("arcane_lens")
            .profile(MaterialFamily.crystal("arcana:arcane_lens").withVolume(0.0001))
            .maxStackSize(1)
            .rarity(ItemRarity.UNCOMMON)
            .category(ItemCategory.MATERIAL)
            .build();

    public static final DeferredItem<ArcaneLens> LENS_ITEM =
            ITEMS.registerItem("arcane_lens", properties -> new ArcaneLens(
                    GenesisApi.itemProperties(LENS).apply(properties)));

    /** A bottle of glass: the altar fills it with the magic of the ground. */
    public static final ItemTemplate VIAL = ItemTemplate.builder("arcane_vial")
            .profile(MaterialFamily.glass("arcana:arcane_vial").withVolume(0.0001))
            .maxStackSize(16)
            .rarity(ItemRarity.UNCOMMON)
            .category(ItemCategory.MATERIAL)
            .build();

    public static final DeferredItem<ArcaneVial> VIAL_ITEM =
            ITEMS.registerItem("arcane_vial", properties -> new ArcaneVial(
                    GenesisApi.itemProperties(VIAL).apply(properties)));

    private ArcanaItems() {
    }

    /** Registers the materials of the items with the registry of Genesis. */
    public static void registerProfiles() {
        MaterialRegistry.register(LENS.profile());
        MaterialRegistry.register(VIAL.profile());
    }
}
