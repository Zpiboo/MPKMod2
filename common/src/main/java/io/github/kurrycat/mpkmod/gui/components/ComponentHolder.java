package io.github.kurrycat.mpkmod.gui.components;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.gui.screens.LandingBlockGuiScreen;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ComponentHolder<C extends Component<?>> {
    protected final Roo<C> children = new ArrayList<>();

    public abstract void updateTree();
    public abstract void layoutTree();

    public abstract Vector2D getDisplayedPos();
    public abstract Vector2D getDisplayedSize();

    public abstract ComponentHolder<?> getRoot();

    public void render(Vector2D mousePos) {
        children.forEach(c -> c.render(mousePos));
    }

    public List<C> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(C child) {
        passPositionTo(child);
        children.add(child);
    }

    public void removeChild(C child) {
        this.children.remove(child);
        child.setParent(null);
        child.markDirty();
    }

    public void clearChildren() {
        while (!getChildren().isEmpty())
            removeChild(getChildren().get(0));
    }

    public void passPositionTo(C child) {
        if (child.getParent() == this) return;

        if (child.getParent() != null)
            child.getParent().removeChild(child);

        // noinspection unchecked
        child.setParent(this);
        child.markDirty();
    }

    public void stretchXBetween(C child, C min, C max) {
        child.minX = min;
        child.maxX = max;
        passPositionTo(child);
    }

    public void stretchYBetween(C child, C min, C max) {
        child.minY = min;
        child.maxY = max;
        passPositionTo(child);
    }
}
