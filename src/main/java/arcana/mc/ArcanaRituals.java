package arcana.mc;

import java.util.Optional;

import arcana.core.ritual.RitualRules;

import genesis.api.magic.MagicConcentration;
import genesis.api.magic.MagicTier;
import genesis.core.magic.MagicField;
import genesis.mc.GenesisApi;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * The rituals of the altar, carried out on the world of Genesis.
 *
 * <p>This is the only place in the mod that talks to the magic of a world. It reads the
 * concentration of the ground, asks the rules what should happen and then changes the ground
 * accordingly — and because the magic of a world is stored, the change is still there after the
 * server has been restarted.
 */
public final class ArcanaRituals {

    private ArcanaRituals() {
    }

    /** The magic of the dimension, or nothing when it is not a Genesis world. */
    public static Optional<MagicField> field(ServerLevel level) {
        return GenesisApi.magicField(level);
    }

    /**
     * Draws the magic of the ground into a bottle.
     *
     * @return the result of the ritual, or nothing when this is not a Genesis world
     */
    public static Optional<arcana.api.RitualResult> drain(ServerLevel level, BlockPos pos) {
        Optional<MagicField> field = field(level);
        if (field.isEmpty()) {
            return Optional.empty();
        }
        MagicField magic = field.get();
        double concentration = magic.concentrationAt(pos.getX(), pos.getZ());
        arcana.api.RitualResult result = RitualRules.drain(concentration);
        if (result.succeeded()) {
            magic.influence(pos.getX(), pos.getZ(), RitualRules.RADIUS, result.groundDelta());
        }
        return Optional.of(result);
    }

    /**
     * Pours magic into the ground.
     *
     * @param offering how much magic is offered
     * @return the result of the ritual, or nothing when this is not a Genesis world
     */
    public static Optional<arcana.api.RitualResult> infuse(ServerLevel level, BlockPos pos, double offering) {
        Optional<MagicField> field = field(level);
        if (field.isEmpty()) {
            return Optional.empty();
        }
        MagicField magic = field.get();
        double concentration = magic.concentrationAt(pos.getX(), pos.getZ());
        arcana.api.RitualResult result = RitualRules.infuse(concentration, offering);
        if (result.succeeded()) {
            magic.influence(pos.getX(), pos.getZ(), RitualRules.RADIUS, result.groundDelta());
        }
        return Optional.of(result);
    }

    /** The concentration of the ground at a position, or nothing outside a Genesis world. */
    public static Optional<MagicConcentration> concentration(ServerLevel level, BlockPos pos) {
        return GenesisApi.concentration(level, pos.getX(), pos.getZ());
    }

    /** The strength of the magic of the ground at a position. */
    public static Optional<MagicTier> tier(ServerLevel level, BlockPos pos) {
        return GenesisApi.tierAt(level, pos.getX(), pos.getZ());
    }
}
