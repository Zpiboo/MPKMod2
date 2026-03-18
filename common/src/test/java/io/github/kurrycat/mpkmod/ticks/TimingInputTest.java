package io.github.kurrycat.mpkmod.ticks;

import io.github.kurrycat.mpkmod.util.Tuple;
import io.github.kurrycat.mpkmod.util.input.InputVector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimingInputTest {
    private void assertFindsCorrectMsButtonPair(ButtonMS.Button expected1, ButtonMS.Button expected2, TimingInput before, TimingInput after, List<TimingInput> curr) {
        assertFindsCorrectMsButtonReturn(
                new Tuple<>(expected1, expected2),
                before, after, curr
        );
    }
    private void assertFindsCorrectMsButtonReturn(Tuple<ButtonMS.Button, ButtonMS.Button> expected, TimingInput before, TimingInput after, List<TimingInput> curr) {
        assertEquals(
                expected,
                TimingInput.findMSButtons(before, after, curr)
        );
    }

    @Test
    void findsCorrectSingleMsButton() {
        TimingInput before = new TimingInput(InputVector.fromString("D"), true, false, GroundState.GROUNDED);

        List<TimingInput> curr = new ArrayList<>();
        curr.add(new TimingInput(InputVector.fromString("WD"), false, false, GroundState.GROUNDED));
        curr.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.JUMPING));
        curr.add(new TimingInput(InputVector.fromString("WA"), true, false, GroundState.AIRBORNE));

        TimingInput after = new TimingInput(InputVector.fromString("A"), true, false, GroundState.AIRBORNE);

        assertFindsCorrectMsButtonPair(ButtonMS.Button.FORWARD, ButtonMS.Button.FORWARD, before, after, curr);
    }

    @Test
    void findMsButtonsFailsIfANonJumpKeyChanges() {
        TimingInput before = new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED);

        List<TimingInput> curr = new ArrayList<>();
        for (int i = 0; i < 2; i++) curr.add(before.copy());
        curr.add(new TimingInput(before.inputVector, before.P, !before.N, before.G));  // changes non-jump key state

        TimingInput after = before.copy();

        assertNull(TimingInput.findMSButtons(before, after, curr));
    }

    @Test
    void findsCorrectInterruptionMsButtons() {
        TimingInput before = new TimingInput(InputVector.fromString(""), false, false, GroundState.GROUNDED);

        List<TimingInput> curr = new ArrayList<>();
        curr.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        curr.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        curr.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));

        TimingInput after = new TimingInput(InputVector.fromString("W"), true, false, GroundState.JUMPING);

        assertFindsCorrectMsButtonPair(ButtonMS.Button.FORWARD, ButtonMS.Button.JUMP, before, after, curr);
    }
}
