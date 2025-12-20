package io.github.kurrycat.mpkmod.gui.components;

import java.util.ArrayList;

public abstract class ComponentHolder extends Component {
    protected ArrayList<Component> components = new ArrayList<>();

    public void addChild(Component child) {
        addChild(child, PERCENT.NONE, Anchor.TOP_LEFT);
    }

    public void addChild(Component child, int percentFlag) {
        addChild(child, percentFlag, Anchor.TOP_LEFT);
    }

    /**
     * @param child       child component to add to parent
     * @param percentFlag flag built of {@link PERCENT} fields that determines which fields of posX, posY, sizeX and sizeY should be treated as a percentage of the parent
     * @param anchor      {@link Anchor}point of both the parent and child
     */
    public void addChild(Component child, int percentFlag, Anchor anchor) {
        addChild(child, percentFlag, anchor, anchor);
    }

    public void addChild(Component child, int percentFlag, Anchor anchor, Anchor parentAnchor) {
        passPositionTo(child, percentFlag, anchor, parentAnchor);
        this.components.add(child);
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
        this.components.remove(child);
        child.setRoot(null);
        child.parent = null;
    }
}
