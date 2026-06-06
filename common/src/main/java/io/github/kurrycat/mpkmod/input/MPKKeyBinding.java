package io.github.kurrycat.mpkmod.input;

import io.github.kurrycat.mpkmod.util.Procedure;

public class MPKKeyBinding {
    public final Procedure action;
    public final boolean isDebug;

    public MPKKeyBinding(Procedure action, boolean isDebug) {
        this.action = action;
        this.isDebug = isDebug;
    }
}
