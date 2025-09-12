package io.github.kurrycat.mpkmod.gui;

import io.github.kurrycat.mpkmod.util.Vector2D;

public class RenderContext {
    public final Vector2D mousePos;
    public boolean canHover;

    public float partialTicks;

    public RenderContext(Vector2D mousePos, float partialTicks) {
        this(mousePos, true, partialTicks);
    }

    public RenderContext(Vector2D mousePos, boolean canHover, float partialTicks) {
        this.mousePos = mousePos;
        this.canHover = canHover;

        this.partialTicks = partialTicks;
    }
}
