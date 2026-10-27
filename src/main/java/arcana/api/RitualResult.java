package arcana.api;

/**
 * The result of a ritual.
 *
 * <p>It is plain data: the rules of a ritual can be worked out without the game, and the game
 * only has to carry the numbers out.
 *
 * @param outcome            what happened
 * @param concentrationBefore the concentration of the ground before the ritual
 * @param concentrationAfter  the concentration of the ground after the ritual
 * @param collected          how much magic was drawn out of the ground
 * @param added              how much magic was poured into the ground
 * @param groundDelta        the change of the ground, negative when it became poorer
 */
public record RitualResult(
        RitualOutcome outcome,
        double concentrationBefore,
        double concentrationAfter,
        double collected,
        double added,
        double groundDelta) {

    public RitualResult {
        if (outcome == null) {
            throw new IllegalArgumentException("A ritual needs an outcome");
        }
    }

    /** Whether the ritual did anything at all. */
    public boolean succeeded() {
        return outcome == RitualOutcome.COLLECTED || outcome == RitualOutcome.OFFERED;
    }

    /** How full the bottle is, between {@code 0} and {@code 1}. */
    public double fill() {
        return collected;
    }
}
