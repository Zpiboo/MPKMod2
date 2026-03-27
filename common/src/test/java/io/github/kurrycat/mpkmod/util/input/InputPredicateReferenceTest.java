package io.github.kurrycat.mpkmod.util.input;

import io.github.kurrycat.mpkmod.ticks.TimingEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputPredicateReferenceTest {
    @Test
    void delegatesMatchProperly() {
        InputPredicate predicate = new InputPredicate("wasd?");
        predicate.matches(new InputVector(true, true, false, false));

        InputPredicateReference predicateReference = new InputPredicateReference(0);

        TimingEntry[] parentTimingEntries = new TimingEntry[] {
                new TimingEntry(null, 1, 1, predicate, null, null, null),
                new TimingEntry(null, 1, 1, predicateReference, null, null, null)
        };
        predicateReference.setParentTimingEntries(parentTimingEntries);

        assertFalse(predicateReference.matches(new InputVector(true, false, false, false)));
        assertTrue(predicateReference.matches(new InputVector(true, true, false, false)));
    }
}
