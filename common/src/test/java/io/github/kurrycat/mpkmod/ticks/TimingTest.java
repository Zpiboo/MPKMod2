package io.github.kurrycat.mpkmod.ticks;

import io.github.kurrycat.mpkmod.util.input.InputPredicate;
import io.github.kurrycat.mpkmod.util.input.InputPredicateReference;
import io.github.kurrycat.mpkmod.util.input.InputVector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TimingTest {
    private static final InputPredicate stop = new InputPredicate(false, false, false, false, false);
    private static final InputPredicate move = new InputPredicate(null, null, null, null, true);

    @Test
    void transmitsEntryArrayToChildInputPredicateReferences() {
        LinkedHashMap<Timing.FormatCondition, Timing.FormatString> format = new LinkedHashMap<>();
        format.put(new Timing.FormatCondition("default"), new Timing.FormatString("pane fmm -{a}t run {r}t"));

        TimingEntry[] timingEntries = new TimingEntry[] {
                new TimingEntry("a", 1, 1, stop, null, null, GroundState.JUMPING),
                new TimingEntry("a", 0, null, stop, null, null, GroundState.AIRBORNE),
                new TimingEntry(null, 0, null, move, null, null, GroundState.AIRBORNE),
                new TimingEntry(null, 1, 1, new InputPredicateReference(2), null, null, GroundState.GROUNDED),
                new TimingEntry("r", 1, null, new InputPredicateReference(2), null, null, GroundState.GROUNDED),
                new TimingEntry(null, 1, 1, new InputPredicateReference(2), null, null, GroundState.JUMPING)
        };
        Timing timing = new Timing(format, timingEntries, false);

        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.JUMPING));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.JUMPING));

        Timing.Match match = timing.match(inputList);
        assertNotNull(match);
    }
}
