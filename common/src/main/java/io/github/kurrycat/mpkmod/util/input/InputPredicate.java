package io.github.kurrycat.mpkmod.util.input;

import com.fasterxml.jackson.annotation.JsonCreator;

public final class InputPredicate extends InputPredicateBase {
    private final AxisConstraint wsConstraint;
    private final AxisConstraint adConstraint;
    private final boolean forbidStop;

    InputVector lastMatchedInput;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public InputPredicate(String inputs) {
        // uppercase -> true (required); lowercase -> null (optional); absent -> false (forbidden)
        this(
                (inputs.indexOf('W') >= 0 ? Boolean.TRUE : (inputs.indexOf('w') >= 0 ? null : Boolean.FALSE)),
                (inputs.indexOf('A') >= 0 ? Boolean.TRUE : (inputs.indexOf('a') >= 0 ? null : Boolean.FALSE)),
                (inputs.indexOf('S') >= 0 ? Boolean.TRUE : (inputs.indexOf('s') >= 0 ? null : Boolean.FALSE)),
                (inputs.indexOf('D') >= 0 ? Boolean.TRUE : (inputs.indexOf('d') >= 0 ? null : Boolean.FALSE)),
                inputs.indexOf('?') >= 0
        );
    }

    public InputPredicate(Boolean W, Boolean A, Boolean S, Boolean D, boolean forbidStop) {
        this(
                AxisConstraint.fromAxisConstraints(W, S),
                AxisConstraint.fromAxisConstraints(A, D),
                forbidStop
        );
    }

    private InputPredicate(AxisConstraint wsConstraint, AxisConstraint adConstraint, boolean forbidStop) {
        this.wsConstraint = wsConstraint;
        this.adConstraint = adConstraint;
        this.forbidStop = forbidStop;
    }

    public void resetLastMatch() {
        lastMatchedInput = null;
    }

    public boolean matches(InputVector input) {
        boolean isMatch = (
                (wsConstraint.allows(input.WS)) &&
                (adConstraint.allows(input.AD)) &&
                !(input.isStop() && forbidStop)
        );
        if (isMatch) lastMatchedInput = input;

        return isMatch;
    }

    public InputPredicate mirrored() {
        return new InputPredicate(wsConstraint, adConstraint.opposite(), forbidStop);
    }

    private enum AxisConstraint {
        REQUIRED_POS(true, false, false),
        OPTIONAL_POS(true, true, false),
        FORBIDDEN(false, true, false),
        ANY(true, true, true),
        OPTIONAL_NEG(false, true, true),
        REQUIRED_NEG(false, false, true);

        private final boolean allowPos;
        private final boolean allowZero;
        private final boolean allowNeg;

        AxisConstraint(boolean allowPos, boolean allowZero, boolean allowNeg) {
            this.allowPos = allowPos;
            this.allowZero = allowZero;
            this.allowNeg = allowNeg;
        }

        public boolean allows(int axisTernary) {
            if (axisTernary > 0) return allowPos;
            if (axisTernary < 0) return allowNeg;
            return allowZero;
        }

        public AxisConstraint opposite() {
            switch (this) {
                case ANY: case FORBIDDEN: return this;

                case REQUIRED_POS: return REQUIRED_NEG;
                case REQUIRED_NEG: return REQUIRED_POS;
                case OPTIONAL_POS: return OPTIONAL_NEG;
                case OPTIONAL_NEG: return OPTIONAL_POS;

                default: return FORBIDDEN;
            }
        }

        public static AxisConstraint fromAxisConstraints(Boolean pos, Boolean neg) {
            if (pos == neg) {
                if (pos == null) return ANY;
                return FORBIDDEN;
            }
            if (pos == null) return neg ? OPTIONAL_NEG : OPTIONAL_POS;
            if (neg == null) return pos ? OPTIONAL_POS : OPTIONAL_NEG;

            if (pos) return REQUIRED_POS;
            if (neg) return REQUIRED_NEG;

            return FORBIDDEN;
        }
    }
}
