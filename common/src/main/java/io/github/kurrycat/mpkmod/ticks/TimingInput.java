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
