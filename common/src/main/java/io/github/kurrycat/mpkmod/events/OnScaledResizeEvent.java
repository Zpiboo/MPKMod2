package io.github.kurrycat.mpkmod.events;

import io.github.kurrycat.mpkmod.util.Vector2D;

public class OnScaledResizeEvent extends Event {
    public final Vector2D scaledSize;

    public OnScaledResizeEvent(Vector2D scaledSize) {
        super(EventType.SCALED_RESIZE);
        this.scaledSize = scaledSize;
    }
}
