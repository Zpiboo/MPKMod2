package io.github.kurrycat.mpkmod.ticks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kurrycat.mpkmod.util.Range;
import io.github.kurrycat.mpkmod.util.input.InputPredicate;
import io.github.kurrycat.mpkmod.util.input.InputPredicateBase;

import java.util.List;

public final class TimingEntry {
    public final String varName;
    public final Range range;

    public final InputPredicateBase inputPredicate;
    public final Boolean P, N;
    public final GroundState G;

    @JsonCreator
    public TimingEntry(
            @JsonProperty("var")
            String varName,
            @JsonProperty("min")
            Integer min,
            @JsonProperty("max")
            Integer max,
            @JsonProperty("inputs")
            InputPredicateBase inputPredicate,
            @JsonProperty("sprint")
            Boolean P,
            @JsonProperty("sneak")
            Boolean N,
            @JsonProperty("groundState")
            GroundState G
    ) {
        if (min == null && max == null) min = max = 1;
        if (min == null || min <= 0) min = 0;
        if (max != null && max < min) max = min;

        this.varName = varName;
        this.range = new Range(min, max);
        this.inputPredicate = inputPredicate;
        this.P = P;
        this.N = N;
        this.G = G;
    }

    public boolean varNameMatches(TimingEntry other) {
        return varName != null && other.varName != null && varName.equals(other.varName);
    }

    private boolean matchesInput(TimingInput timingInput) {
        return (
                (inputPredicate == null || inputPredicate.matches(timingInput.inputVector)) &&
                (P == null || P == timingInput.P) &&
                (N == null || N == timingInput.N) &&
                (G == null || G == timingInput.G)
        );
    }

    /**
     * @param inputList List of {@link TimingInput} instances
     * @param startIndex the index it should start matching from
     * @param ctx HashMap containing all variables for that {@link Timing}. Vars of this TimingEntry are added
     * @param repeatedVar if the current var appeared in the previous {@link TimingEntry}
     * @return amount of matched inputs or null if no match was found (returns 0 for variable entries with 0 within range)
     */
    public Integer matches(List<TimingInput> inputList, int startIndex, VariableContext ctx, boolean repeatedVar) {
        int i = startIndex;

        if (inputPredicate instanceof InputPredicate) ((InputPredicate) inputPredicate).resetLastMatch();
        while (i < inputList.size() && matchesInput(inputList.get(i))) i++;
        int count = range.constrain(i - startIndex);

        if (range.isValueBelow(i - startIndex)) return null;

        if (varName != null) {
            if (ctx.tickVars.containsKey(varName) && repeatedVar) {
                ctx.tickVars.put(varName, ctx.tickVars.get(varName) + count);
            } else {
                ctx.tickVars.put(varName, count);
            }
        }

        return count;
    }

    public TimingEntry mirrored() {
        InputPredicateBase mirroredInputPredicate = inputPredicate;
        if (inputPredicate instanceof InputPredicate)
            mirroredInputPredicate = ((InputPredicate) inputPredicate).mirrored();

        return new TimingEntry(varName, range.getLower(), range.getUpper(), mirroredInputPredicate, P, N, G);
    }
}
