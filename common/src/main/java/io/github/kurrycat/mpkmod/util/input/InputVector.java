package io.github.kurrycat.mpkmod.util.input;

public final class InputVector {
    public static final InputVector ZERO = new InputVector(false, false, false, false);
    public final int WS;
    public final int AD;

    public InputVector(boolean W, boolean A, boolean S, boolean D) {
        WS = (W ? 1 : 0) - (S ? 1 : 0);
        AD = (A ? 1 : 0) - (D ? 1 : 0);
    }
    public InputVector(int WS, int AD) {
        this.WS = Integer.compare(WS, 0);
        this.AD = Integer.compare(AD, 0);
    }

    public boolean isW() {
        return WS > 0;
    }
    public boolean isA() {
        return AD > 0;
    }
    public boolean isS() {
        return WS < 0;
    }
    public boolean isD() {
        return AD < 0;
    }

    public boolean isStop() {
        return WS == 0 && AD == 0;
    }

    public static InputVector fromString(String inputString) {
        if (inputString == null) return null;

        boolean W = inputString.indexOf('W') >= 0;
        boolean A = inputString.indexOf('A') >= 0;
        boolean S = inputString.indexOf('S') >= 0;
        boolean D = inputString.indexOf('D') >= 0;

        return new InputVector(W, A, S, D);
    }

    @Override
    public String toString() {
        return (
                (WS == 0 ? "" : (isW() ? "W" : "S")) +
                (AD == 0 ? "" : (isA() ? "A" : "D"))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputVector that = (InputVector) o;

        return WS == that.WS && AD == that.AD;
    }

    @Override
    public int hashCode() {
        return 3 * WS + AD;
    }
}
