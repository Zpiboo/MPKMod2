package io.github.kurrycat.mpkmod.util;

public class ScissorBox {
    public double x, y, w, h;

    public ScissorBox(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public boolean contains(Vector2D point) {
        return x <= point.getX() && point.getX() < x + w &&
                y <= point.getY() && point.getY() < y + h;
    }
}