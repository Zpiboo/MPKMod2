package io.github.kurrycat.mpkmod.input;

import io.github.kurrycat.mpkmod.util.Procedure;

public class MPKKeyBinding {
    public final Procedure action;
    public final int defaultKeycode;
    public final boolean isDebug;

    public MPKKeyBinding(Procedure action, int defaultKeycode, boolean isDebug) {
        this.action = action;
        this.defaultKeycode = defaultKeycode;
        this.isDebug = isDebug;
    }
}
