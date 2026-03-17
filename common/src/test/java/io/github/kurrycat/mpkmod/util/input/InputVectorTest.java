package io.github.kurrycat.mpkmod.util.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InputVectorTest {
    @Test
    void oppositeInputKeysCancelOut() {
        InputVector inputVector = new InputVector(true, false, true, true);

        assertFalse(inputVector.isW());
        assertFalse(inputVector.isA());
        assertFalse(inputVector.isS());
        assertTrue(inputVector.isD());
    }

    @Test
    void cancelledKeysProduceCorrectVector() {
        InputVector inputVector = new InputVector(true, false, true, true);

        assertEquals(0, inputVector.WS);
        assertEquals(-1, inputVector.AD);
    }
}
