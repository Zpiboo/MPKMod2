package io.github.kurrycat.mpkmod.util.input;

import io.github.kurrycat.mpkmod.ticks.TimingEntry;

public class InputPredicateReference extends InputPredicateBase {
    private final int index;
    private TimingEntry[] parentTimingEntries;
    private InputPredicate referencedPredicate;

    public InputPredicateReference(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setParentTimingEntries(TimingEntry[] parentTimingEntries) {
        if (parentTimingEntries == null) return;

        if (this.parentTimingEntries != null)
            throw new IllegalStateException("Parent timing entry array is already set");

        this.parentTimingEntries = parentTimingEntries;

        if (parentTimingEntries[index].inputPredicate.getClass().equals(InputPredicate.class)) {
            this.referencedPredicate = (InputPredicate) parentTimingEntries[index].inputPredicate;
        } else {
            this.referencedPredicate = null;
        }
    }

    @Override
    public boolean matches(InputVector input) {
        if (referencedPredicate == null) return false;
        return referencedPredicate.matches(input);
    }
}
