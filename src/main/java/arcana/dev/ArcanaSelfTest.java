package arcana.dev;

import arcana.api.RitualOutcome;
import arcana.api.RitualResult;
import arcana.core.ritual.RitualRules;

/**
 * The self test of the rules of the altar.
 *
 * <p>It runs against a narrow class path — only the classes of this mod and the standard library
 * — which proves that the rules of a ritual have nothing to do with the game. The game only
 * carries out what these rules decide.
 */
public final class ArcanaSelfTest {

    private static int checks;
    private static int failures;

    private ArcanaSelfTest() {
    }

    public static void main(String[] args) {
        System.out.println("== The rules of the altar ==");
        testDraining();
        testInfusing();
        testConservation();

        System.out.println("checks  : " + checks);
        System.out.println("failures: " + failures);
        if (failures > 0) {
            System.out.println("ARCANA SELF TEST FAILED");
            System.exit(1);
        }
        System.out.println("ARCANA SELF TEST PASSED");
    }

    private static void testDraining() {
        RitualResult empty = RitualRules.drain(0.2);
        check("a poor ground has nothing to collect", empty.outcome() == RitualOutcome.TOO_FEW);
        check("a poor ground is left alone", empty.groundDelta() == 0.0);
        check("a failed ritual changes nothing", empty.concentrationAfter() == empty.concentrationBefore());

        RitualResult normal = RitualRules.drain(1.0);
        double expected = (1.0 - RitualRules.MINIMUM) * RitualRules.EFFICIENCY;
        check("a normal ground gives up its magic", normal.outcome() == RitualOutcome.COLLECTED);
        check("what is collected follows the rules", Math.abs(normal.collected() - expected) < 1.0e-9);
        check("the ground becomes poorer", normal.groundDelta() < 0.0);
        check("the ground keeps what was not collected",
                Math.abs(normal.concentrationAfter() - (1.0 - expected)) < 1.0e-9);

        RitualResult rich = RitualRules.drain(5.0);
        check("a bottle cannot hold more than its capacity", rich.collected() <= RitualRules.CAPACITY);
        check("a rich ground is drained up to the capacity",
                Math.abs(rich.collected() - RitualRules.CAPACITY) < 1.0e-9);

        check("a bottle is never overfilled", RitualRules.fillOf(RitualRules.CAPACITY) <= 1.0);
        check("an empty bottle is not full", RitualRules.fillOf(0.0) == 0.0);
        check("a full bottle is full", Math.abs(RitualRules.fillOf(RitualRules.CAPACITY) - 1.0) < 1.0e-9);
    }

    private static void testInfusing() {
        RitualResult nothing = RitualRules.infuse(1.0, 0.0);
        check("a material without magic is refused", nothing.outcome() == RitualOutcome.NOT_MAGIC);
        check("a refused offering changes nothing", nothing.groundDelta() == 0.0);

        RitualResult offered = RitualRules.infuse(1.0, 0.5);
        check("an offering goes into the ground", offered.outcome() == RitualOutcome.OFFERED);
        check("the ground becomes richer", offered.groundDelta() > 0.0);
        check("what is offered is what the ground gains",
                Math.abs(offered.added() - 0.5) < 1.0e-9);
        check("the ground keeps the rest of its magic",
                Math.abs(offered.concentrationAfter() - 1.5) < 1.0e-9);

        RitualResult saturated = RitualRules.infuse(RitualRules.SATURATION, 1.0);
        check("a saturated ground takes nothing more", saturated.outcome() == RitualOutcome.SATURATED);
        check("a saturated ground is left alone", saturated.groundDelta() == 0.0);

        RitualResult almost = RitualRules.infuse(RitualRules.SATURATION - 0.2, 1.0);
        check("a ground that is nearly full only takes what fits",
                Math.abs(almost.added() - 0.2) < 1.0e-9);
        check("a nearly full ground is not reported as saturated",
                almost.outcome() == RitualOutcome.OFFERED);
    }

    private static void testConservation() {
        // What is drawn out of one region and poured into another does not grow on the way.
        double before = 1.4;
        RitualResult taken = RitualRules.drain(before);
        RitualResult given = RitualRules.infuse(0.6, taken.collected());
        check("magic is not created on the way", given.added() <= taken.collected() + 1.0e-9);
        check("the two halves of a ritual agree on the amount",
                Math.abs(given.added() - taken.collected()) < 1.0e-9);
        check("draining and infusing are not the same thing",
                taken.groundDelta() < 0.0 && given.groundDelta() > 0.0);
    }

    private static void check(String name, boolean condition) {
        checks++;
        if (condition) {
            System.out.println("  [ok]   " + name);
        } else {
            failures++;
            System.out.println("  [FAIL] " + name);
        }
    }
}
