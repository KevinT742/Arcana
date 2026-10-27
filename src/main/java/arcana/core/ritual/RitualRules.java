package arcana.core.ritual;

import arcana.api.RitualOutcome;
import arcana.api.RitualResult;

/**
 * The rules of the rituals of the altar.
 *
 * <p>These rules are the whole of the magic of the altar, and they know nothing about the game:
 * a ritual is a function of the concentration of the ground and of what is offered. The game only
 * has to read the concentration, work out the result and carry it out.
 *
 * <p>The ground of a region is not a bucket that can be emptied: below a certain concentration
 * there is nothing left worth collecting, and above a certain one it cannot hold any more.
 */
public final class RitualRules {

    /** Below this concentration the ground is empty: there is nothing worth collecting. */
    public static final double MINIMUM = 0.45;

    /** How much magic one ritual can draw into a bottle. */
    public static final double CAPACITY = 1.2;

    /**
     * How much of what is there can actually be collected.
     *
     * <p>The rest is lost: the magic of the ground is not a liquid that can be poured, and part
     * of it slips through the fingers.
     */
    public static final double EFFICIENCY = 0.55;

    /** Above this concentration the ground cannot hold any more. */
    public static final double SATURATION = 3.2;

    /** How large an area one ritual reaches. */
    public static final double RADIUS = 24.0;

    private RitualRules() {
    }

    /**
     * Draws the magic of the ground into a bottle.
     *
     * <p>The ground becomes poorer by what was collected, and the bottle carries it away.
     *
     * @param concentration the concentration of the ground where the ritual is done
     * @return what happened
     */
    public static RitualResult drain(double concentration) {
        double before = Math.max(0.0, concentration);
        if (before < MINIMUM) {
            return new RitualResult(RitualOutcome.TOO_FEW, before, before, 0.0, 0.0, 0.0);
        }
        double collected = Math.min((before - MINIMUM) * EFFICIENCY, CAPACITY);
        double after = Math.max(0.0, before - collected);
        return new RitualResult(RitualOutcome.COLLECTED, before, after, collected, 0.0, -collected);
    }

    /**
     * Pours magic into the ground.
     *
     * <p>What is offered is usually a magic material, and its magic becomes the magic of the
     * ground. A ground that is already saturated cannot take any more, and the rest is wasted.
     *
     * @param concentration the concentration of the ground where the ritual is done
     * @param offering      how much magic is offered
     * @return what happened
     */
    public static RitualResult infuse(double concentration, double offering) {
        double before = Math.max(0.0, concentration);
        if (offering <= 0.0) {
            return new RitualResult(RitualOutcome.NOT_MAGIC, before, before, 0.0, 0.0, 0.0);
        }
        if (before >= SATURATION) {
            return new RitualResult(RitualOutcome.SATURATED, before, before, 0.0, 0.0, 0.0);
        }
        double added = Math.min(offering, SATURATION - before);
        return new RitualResult(RitualOutcome.OFFERED, before, before + added, 0.0, added, added);
    }

    /**
     * How full a bottle is, between {@code 0} and {@code 1}.
     *
     * @param collected how much magic the bottle carries
     */
    public static double fillOf(double collected) {
        if (collected <= 0.0) {
            return 0.0;
        }
        return Math.min(1.0, collected / CAPACITY);
    }
}
