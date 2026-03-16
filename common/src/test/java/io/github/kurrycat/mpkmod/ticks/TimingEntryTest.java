package io.github.kurrycat.mpkmod.ticks;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kurrycat.mpkmod.util.Range;
import io.github.kurrycat.mpkmod.util.input.InputPredicate;
import io.github.kurrycat.mpkmod.util.input.InputVector;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimingEntryTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static TimingEntry createSprintStrafeTimingEntry() {
        return new TimingEntry(
                "x", 0, null,
                new InputPredicate("WD"),
                true,
                null,
                GroundState.GROUNDED
        );
    }

    private static List<TimingInput> createWadInputList() {
        List<TimingInput> wadInputs = new ArrayList<>();

        wadInputs.add(TimingInput.stopTick());
        wadInputs.add(TimingInput.stopTick());
        wadInputs.add(new TimingInput(InputVector.fromString("WD"), true, true, GroundState.GROUNDED));
        wadInputs.add(new TimingInput(InputVector.fromString("WD"), true, false, GroundState.GROUNDED));
        wadInputs.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.JUMPING));
        wadInputs.add(new TimingInput(InputVector.fromString("WA"), true, false, GroundState.AIRBORNE));

        return wadInputs;
    }

    @Test
    void deserializesTimingEntryCorrectly() throws IOException {
        String json = String.join("\n",
                "{",
                "  \"var\": \"foo\",",
                "  \"min\": 1,",
                "  \"max\": 5,",
                "  \"inputs\": \"WASD\",",
                "  \"sprint\": false,",
                "  \"sneak\": true,",
                "  \"groundState\": \"JUMPING\"",
                "}"
        );

        TimingEntry e = mapper.readValue(json, TimingEntry.class);

        assertEquals("foo", e.varName);
        assertEquals(new Range(1, 5), e.range);
        assertNotNull(e.inputPredicate);
        assertFalse(e.P);
        assertTrue(e.N);
        assertEquals(GroundState.JUMPING, e.G);
    }

    @Test
    void deserializesTimingEntryDefaultsCorrectly() throws IOException {
        String json = "{}";

        TimingEntry e = mapper.readValue(json, TimingEntry.class);

        assertNull(e.varName);
        assertEquals(new Range(1, 1), e.range);
        assertNull(e.inputPredicate);
        assertNull(e.P);
        assertNull(e.N);
        assertNull(e.G);
    }

//    @Test
    void getMsMethodWorks() {
        final int FIRST_MS = 3;
        final int SECOND_MS = 69;

        TimingEntry timingEntry = new TimingEntry(
                "x", 0, null,
                new InputPredicate("W"),
                null,
                null,
                GroundState.GROUNDED
        );

        List<TimingInput> inputList = new ArrayList<>();
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.GROUNDED));
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.JUMPING));
        inputList.add(new TimingInput(InputVector.fromString("W"), true, false, GroundState.AIRBORNE));

        inputList.get(0).msList.add(ButtonMS.of(ButtonMS.Button.FORWARD, 1_000_000 * FIRST_MS, true));
        inputList.get(1).msList.add(ButtonMS.of(ButtonMS.Button.JUMP, 1_000_000 * SECOND_MS, true));

        timingEntry.matches(inputList, 0, new VariableContext(), false);
//        assertEquals(SECOND_MS - FIRST_MS, vars.get("x").ms);
    }

    @Test
    void matchesMethodCreatesVariablesProperly() {
        VariableContext ctx = new VariableContext();

        Integer matchCount = createSprintStrafeTimingEntry().matches(createWadInputList(), 2, ctx, false);

        assertEquals(2, matchCount);
        assertEquals(2, ctx.tickVars.get("x"));
    }

    @Test
    void matchesMethodCumulatesVariablesProperly() {
        VariableContext ctx = new VariableContext();
        ctx.tickVars.put("x", 2);

        Integer matchCount = createSprintStrafeTimingEntry().matches(createWadInputList(), 2, ctx, true);

        assertEquals(2, matchCount);
        assertEquals(4, ctx.tickVars.get("x"));
    }
}
