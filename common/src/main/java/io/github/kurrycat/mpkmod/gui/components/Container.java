package io.github.kurrycat.mpkmod.gui.components;

import io.github.kurrycat.mpkmod.util.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class Container extends Component {
    private final List<Component> children = new ArrayList<>();

    @Override
    public void render(Vector2D mouse) {
        for (Component child : getChildren())
            child.render(mouse);
    }

    @Override
    public void setParent(Component parent) {
        super.setParent(parent);

        for (Component child : getChildren())
            child.setParent(this);
    }

    public List<Component> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * @param child child component to add
     */
    public void addChild(Component child) {
        children.add(child);
        passPositionTo(child);
    }

    public void passPositionTo(Component child, int percentFlag, Anchor anchor, Anchor parentAnchor) {
        child.parentAnchor = parentAnchor;
        child.anchor = anchor;
        passPositionTo(child, percentFlag);
    }

    public void passPositionTo(Component child, int percentFlag) {
        child.percentFlag = percentFlag;
        passPositionTo(child);
    }

    public void passPositionTo(Component child) {
        child.setParent(this);
        child.updatePosAndSize();
    }

    public void stretchXBetween(Component child, Component min, Component max) {
        child.minX = min;
        child.maxX = max;
        passPositionTo(child);
    }

    public void stretchYBetween(Component child, Component min, Component max) {
        child.minY = min;
        child.maxY = max;
        passPositionTo(child);
    }

    public void passPositionTo(Component child, int percentFlag, Anchor anchor) {
        passPositionTo(child, percentFlag, anchor, anchor);
    }

    public void removeChild(Component child) {
        if (!children.remove(child)) return;
        child.setParent(null);
    }

    public void clearChildren() {
        while (!getChildren().isEmpty())
            removeChild(getChildren().get(0));
    }
}
