package arcana.api;

/**
 * What came out of a ritual.
 *
 * <p>The outcome says what happened, so that the game can tell the player and the tests can
 * check it without touching the game.
 */
public enum RitualOutcome {

    /** Magic was drawn out of the ground into a bottle. */
    COLLECTED,

    /** Magic was poured into the ground. */
    OFFERED,

    /** The ground is too poor: there was nothing worth collecting. */
    TOO_FEW,

    /** The ground is already saturated: the offering was mostly wasted. */
    SATURATED,

    /** What was offered carries no magic at all. */
    NOT_MAGIC
}
