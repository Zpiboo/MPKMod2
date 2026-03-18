package io.github.kurrycat.mpkmod.ticks;

import io.github.kurrycat.mpkmod.util.input.InputPredicate;
import io.github.kurrycat.mpkmod.util.input.InputPredicateReference;
import io.github.kurrycat.mpkmod.util.input.InputVector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimingTest {
    private static final InputPredicate stop = new InputPredicate(false, false, false, false, false);
    private static final InputPredicate move = new InputPredicate(null, null, null, null, true);
    private static final InputPredicate onlyW = new InputPredicate(true, false, false, false, true);
    private static final InputPredicate onlyA = new InputPredicate(false, true, false, false, true);
    private static final InputPredicate onlyS = new InputPredicate(false, false, true, false, true);
    private static final InputPredicate onlyD = new InputPredicate(false, false, false, true, true);

    private static final Timing basicTiming;
    private static final Timing paneFmmTiming;

    static {
        LinkedHashMap<Timing.FormatCondition, Timing.FormatString> paneFmmFormat = new LinkedHashMap<>();
        paneFmmFormat.put(new Timing.FormatCondition("default"), new Timing.FormatString("pane fmm -{a}t run {r}t"));

        TimingEntry[] paneFmmTimingEntries = new TimingEntry[] {
                new TimingEntry("a", 1, 1, stop, null, null, GroundState.JUMPING),
                new TimingEntry("a", 0, null, stop, null, null, GroundState.AIRBORNE),
                new TimingEntry(null, 0, null, move, null, null, GroundState.AIRBORNE),
                new TimingEntry(null, 1, 1, new InputPredicateReference(2), null, null, GroundState.GROUNDED),
                new TimingEntry("r", 1, null, new InputPredicateReference(2), null, null, GroundState.GROUNDED),
                new TimingEntry(null, 1, 1, new InputPredicateReference(2), null, null, GroundState.JUMPING)
        };

        paneFmmTiming = new Timing(paneFmmFormat, paneFmmTimingEntries, false);


        LinkedHashMap<Timing.FormatCondition, Timing.FormatString> basicFormat = new LinkedHashMap<>();
        basicFormat.put(new Timing.FormatCondition("default"), new Timing.FormatString("basic timing idk"));

        TimingEntry[] basicTimingEntries = new TimingEntry[] {
                new TimingEntry(null, 1, 1, onlyW, null, null, GroundState.GROUNDED),
                new TimingEntry(null, 1, 1, onlyD, null, null, GroundState.GROUNDED),
                new TimingEntry(null, 1, 1, onlyA, null, null, GroundState.GROUNDED)
        };

        basicTiming = new Timing(basicFormat, basicTimingEntries, true);
    }

    @Test
    void basicTimingMatches() {
        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(TimingInput.stopTick());
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("D"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("A"), true, false, GroundState.GROUNDED));

        Timing.Match match = basicTiming.match(inputList);
        assertNotNull(match);
    }

    @Test
    void createsMirroredTimingProperly() {
        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(TimingInput.stopTick());
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("A"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("D"), true, false, GroundState.GROUNDED));

        Timing.Match match = basicTiming.getMirrored().match(inputList);
        assertNotNull(match);
    }

    @Test
    void transmitsEntryArrayToChildInputPredicateReferences() {
        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.JUMPING));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.JUMPING));

        Timing.Match match = paneFmmTiming.match(inputList);
        assertNotNull(match);
    }

    @Test
    void failsIfChildInputPredicateReferenceConditionNotMet() {
        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.JUMPING));
        inputList.add(new TimingInput(InputVector.ZERO, false, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.AIRBORNE));
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.JUMPING));

        Timing.Match match = paneFmmTiming.match(inputList);
        assertNull(match);
    }
}
