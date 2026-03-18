package io.github.kurrycat.mpkmod.util;

import java.util.Objects;

public class Range {
    private final Integer lower;
    private final Integer upper;

    public Range(Integer lower, Integer upper) {
        if (lower == null || upper == null) {
            this.lower = lower;
            this.upper = upper;
        } else {
            this.lower = Math.min(lower, upper);
            this.upper = Math.max(lower, upper);
        }
    }

    public Integer getLower() {
        return lower;
    }

    public Integer getUpper() {
        return upper;
    }

    public boolean includes(int v) {
        if (upper == null && lower == null) return true;
        else if (upper == null) return v >= lower;
        else if (lower == null) return v <= upper;
        else return v >= lower && v <= upper;
    }

    public int constrain(int v) {
        if (lower != null && v < lower) return lower;
        if (upper != null && v > upper) return upper;
        return v;
    }

    public boolean isValueBelow(int v) {
        if (lower == null) return false;
        return v < lower;
    }

    public boolean isValueAbove(int v) {
        if (upper == null) return false;
        return v > upper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range that = (Range) o;

        return Objects.equals(lower, that.lower) && Objects.equals(upper, that.upper);
    }
}
