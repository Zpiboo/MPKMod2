package io.github.kurrycat.mpkmod.ticks;

import io.github.kurrycat.mpkmod.util.Copyable;
import io.github.kurrycat.mpkmod.util.Tuple;
import io.github.kurrycat.mpkmod.util.input.InputVector;

import java.util.List;
import java.util.stream.Collectors;

public class TimingInput implements Copyable<TimingInput> {
    public final InputVector inputVector;
    public final boolean P, N;
    public final GroundState G;
    public final ButtonMSList msList = new ButtonMSList();

    public TimingInput(boolean W, boolean A, boolean S, boolean D, boolean P, boolean N, boolean jump, boolean ground) {
        this(
                new InputVector(W, A, S, D),
                P, N, GroundState.fromBooleans(jump, ground)
        );
    }
    public TimingInput(InputVector inputVector, boolean P, boolean N, GroundState G) {
        this.inputVector = inputVector;
        this.P = P;
        this.N = N;
        this.G = G;
    }

    public static TimingInput stopTick() {
        return new TimingInput(false, false, false, false, false, false, false, true);
    }

    public static Tuple<ButtonMS.Button, ButtonMS.Button> findMSButtons(TimingInput before, TimingInput after, List<TimingInput> curr) {
        boolean[] befInputs = before.inputBoolList();
        boolean[] aftInputs = after.inputBoolList();
        List<boolean[]> curInputsList = curr.stream().map(TimingInput::inputBoolList).collect(Collectors.toList());

        ButtonMS.Button[] allButtons = ButtonMS.Button.values();

        int onlyPressedCurr = findSingleOnlyPressedCurr(befInputs, aftInputs, curInputsList);
        if (onlyPressedCurr != -1)
            return new Tuple<>(allButtons[onlyPressedCurr], allButtons[onlyPressedCurr]);

        for (int i = 0; i < curr.size() - 1; i++) {
            if (!curr.get(i).equalsIgnoreJump(curr.get(i + 1)))
                return null;
        }

        boolean[] curInputs = curr.get(0).inputBoolList();

        Tuple<Integer, Integer> interruptedByMovMod = findInterruptedByMove(befInputs, curInputs, aftInputs);

        if (interruptedByMovMod.getFirst() != -1 && interruptedByMovMod.getSecond() != -1)
            return new Tuple<>(allButtons[interruptedByMovMod.getFirst()], allButtons[interruptedByMovMod.getSecond()]);

        return null;
    }

    public boolean[] inputBoolList() {
        return new boolean[] {
                inputVector.isW(),
                inputVector.isA(),
                inputVector.isS(),
                inputVector.isD(),
                P, N, G.jump
        };
    }

    //returns -1 on multiple matches
    private static int findSingleOnlyPressedCurr(boolean[] befInputs, boolean[] aftInputs, List<boolean[]> curInputs) {
        int index = -1;
        for (int i = 0; i < befInputs.length - 1; i++) {
            int finalI = i;
            if (!befInputs[i] && curInputs.stream().allMatch(b -> b[finalI]) && !aftInputs[i]) {
                if (index == -1) {
                    index = i;
                }
                //multiple matches
                else return -1;
            }
        }
        return index;
    }

    public boolean equalsIgnoreJump(TimingInput other) {
        return inputVector.equals(other.inputVector) && P == other.P && N == other.N;
    }

    //first and second can be -1
    private static Tuple<Integer, Integer> findInterruptedByMove(boolean[] befInputs, boolean[] curInputs, boolean[] aftInputs) {
        int first = findMovButtonDiff(befInputs, curInputs);
        int second = findFirstButtonDiff(curInputs, aftInputs);

        return new Tuple<>(first, second);
    }

    //ignores movMod keys (P,N), returns J for WP -> WAPJ (returns last)
    private static int findMovButtonDiff(boolean[] befInputs, boolean[] curInputs) {
        int diffIndex = -1;

        for (int i : ButtonMS.Button.ONLY_MOVE_INDICES) {
            if (befInputs[i] != curInputs[i]) {
                diffIndex = i;
            }
        }
        return diffIndex;
    }

    private static int findFirstButtonDiff(boolean[] curInputs, boolean[] aftInputs) {
        for (int i : ButtonMS.Button.ALL) {
            if (!curInputs[i] && aftInputs[i]) {
                return i;
            }
        }
        return -1;
    }

    public boolean isStopTick() {
        return inputVector.isStop() && !P && !N && G == GroundState.GROUNDED;
    }

    @Override
    public int hashCode() {
        int h = inputVector.hashCode();
        h = 31 * h + (P ? 1 : 0);
        h = 31 * h + (N ? 1 : 0);
        h = 31 * h + G.ordinal();
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimingInput that = (TimingInput) o;

        return inputVector.equals(that.inputVector) && P == that.P && N == that.N && G == that.G;
    }

    @Override
    public String toString() {
        return toInputString() + (G.ground ? "G" : "!G");
    }

    public String toInputString() {
        return inputVector.toString() +
                (P ? "P" : "") +
                (N ? "N" : "") +
                (G.jump ? "J" : "");
    }

    @Override
    public TimingInput copy() {
        return new TimingInput(inputVector, P, N, G);
    }
}
