package io.github.kurrycat.mpkmod.ticks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kurrycat.mpkmod.util.input.InputKey;

public final class MsEntry {
    private final int timingEntryIndex;
    private final InputKey inputKey;

    @JsonCreator
    public MsEntry(
            @JsonProperty("index")
            int timingEntryIndex,
            @JsonProperty("inputKey")
            InputKey inputKey
    ) {
        this.timingEntryIndex = timingEntryIndex;
        this.inputKey = inputKey;
    }
}
