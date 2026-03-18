package io.github.kurrycat.mpkmod.util.input;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputPredicateTest {
    static final InputVector VEC_ZERO = InputVector.fromString("");
    static final InputVector VEC_W = InputVector.fromString("W");
    static final InputVector VEC_A = InputVector.fromString("A");
    static final InputVector VEC_S = InputVector.fromString("S");
    static final InputVector VEC_D = InputVector.fromString("D");
    static final InputVector VEC_WA = InputVector.fromString("WA");
    static final InputVector VEC_WD = InputVector.fromString("WD");
    static final InputVector VEC_SA = InputVector.fromString("SA");
    static final InputVector VEC_SD = InputVector.fromString("SD");

    @ParameterizedTest
    @MethodSource("exactMatchCases")
    @MethodSource("someMatchCases")
    @MethodSource("anyMatchCases")
    void inputPredicateMatchesVectorTest(boolean expected, String predicateString, InputVector v) {
        InputPredicate p = new InputPredicate(predicateString);
        assertEquals(expected, p.matches(v));
    }

    static Stream<Arguments> exactMatchCases() {
        return Stream.of(
                Arguments.of(false, "WD", VEC_W),
                Arguments.of(false, "WD", VEC_WA),
                Arguments.of(true, "WD", VEC_WD),

                Arguments.of(false, "A", VEC_D),
                Arguments.of(false, "A", VEC_SA),
                Arguments.of(true, "A", VEC_A)
        );
    }

    static Stream<Arguments> someMatchCases() {
        return Stream.of(
                Arguments.of(false, "wad?", VEC_S),
                Arguments.of(false, "wad?", VEC_SD),
                Arguments.of(false, "wad?", VEC_ZERO),
                Arguments.of(true, "wad?", VEC_WD),
                Arguments.of(true, "wad?", VEC_A),

                Arguments.of(false, "w?", VEC_A),
                Arguments.of(false, "w?", VEC_WA),
                Arguments.of(true, "w?", VEC_W)
        );
    }

    static Stream<Arguments> anyMatchCases() {
        return Stream.of(
                Arguments.of(true, "wasd", VEC_S),
                Arguments.of(true, "wasd", VEC_SD),
                Arguments.of(true, "wasd", VEC_WA),
                Arguments.of(true, "wasd", VEC_ZERO),

                Arguments.of(false, "wad", VEC_SA),
                Arguments.of(true, "wad", VEC_WA),
                Arguments.of(true, "wad", VEC_ZERO),

                Arguments.of(false, "", VEC_W),
                Arguments.of(true, "", VEC_ZERO)
        );
    }
}