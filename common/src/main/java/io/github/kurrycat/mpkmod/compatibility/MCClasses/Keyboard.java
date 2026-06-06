package io.github.kurrycat.mpkmod.compatibility.MCClasses;

import io.github.kurrycat.mpkmod.compatibility.API;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Keyboard {
    public static Set<Integer> getPressedButtons() {
        return Interface.get().map(Interface::getPressedButtons).orElseGet(HashSet::new);
    }

    public enum Modifier {
        CTRL,
        SHIFT,
        ALT,
        RCTRL,
        RSHIFT;
    }

    public interface Interface extends FunctionHolder {
        static Optional<Interface> get() {
            return API.getFunctionHolder(Interface.class);
        }

        Set<Integer> getPressedButtons();
    }
}
