package io.github.kurrycat.mpkmod.gui.components;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.util.Debug;
import io.github.kurrycat.mpkmod.util.MathUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.util.Collections;
import java.util.List;

public abstract class Component {
    private Component parent = null;
    private Component root = null;
    /**
     * relative position, always positive, can be percentage
     */
    protected Vector2D pos = Vector2D.ZERO.copy();
    /**
     * absolute position used for rendering
     */
    protected Vector2D cpos = Vector2D.ZERO.copy();
    /**
     * relative size, always positive, can be percentage
     */
    protected Vector2D size = Vector2D.ZERO.copy();
    /**
     * absolute size used for rendering
     */
    protected Vector2D csize = Vector2D.ZERO.copy();
    /**
     * flag that determines whether posX, posY, sizeX and/or sizeY should be treated as a percentage of parent<br>
     * Just use {@link PERCENT} fields to set this (e.g. {@code PERCENT.POS_Y | PERCENT.SIZE_Y}
     * would make it so that posY and sizeY are treated as a percentage)
     */
    protected int percentFlag = PERCENT.NONE;
    /**
     * origin anchor for parent
     */
    protected Anchor parentAnchor = Anchor.TOP_LEFT;
    /**
     * origin anchor for this
     */
    protected Anchor anchor = Anchor.TOP_LEFT;
    protected boolean absolute = false;
    protected long lastUpdated = 0;
    protected Component minX = null;
    protected Component minY = null;
    protected Component maxX = null;
    protected Component maxY = null;

    public Component getRoot() {
        return root;
    }

    public boolean contains(Vector2D testPos) {
        if (testPos == null) return false;
        if (getDisplayedPos() == null) return false;
        return Renderer2D.scissorContains(testPos) &&
                testPos.isInRectBetween(getDisplayedPos(), getDisplayedPos().add(getDisplayedSize()));
    }

    protected long getLastUpdated() {
        if (rParent() == null) return lastUpdated;
        long updated = Math.max(lastUpdated, rParent().getLastUpdated());
        if (minX != null) updated = Math.max(updated, minX.getLastUpdated());
        if (maxX != null) updated = Math.max(updated, maxX.getLastUpdated());
        if (minY != null) updated = Math.max(updated, minY.getLastUpdated());
        if (maxY != null) updated = Math.max(updated, maxY.getLastUpdated());
        return updated;
    }

    public Vector2D getDisplayedPos() {
        if (parent != null && parent.getLastUpdated() > lastUpdated || getRoot() != null && getRoot().getLastUpdated() > lastUpdated)
            updatePosAndSize();
        return this.cpos;
    }

    /**
     * Updates size and pos based on parent size.
     */
    public void updatePosAndSize() {
        if (parent != null) root = parent.root;
        Component p = rParent();
        if (p == null || p == this) {
            this.csize.set(this.size);
            this.cpos.set(this.pos);

            lastUpdated = System.nanoTime();
            return;
        }

        double pX = p.getDisplayedPos().getX(), pY = p.getDisplayedPos().getY();
        double pW = p.getDisplayedSize().getX(), pH = p.getDisplayedSize().getY();

        double w = this.size.getX(), h = this.size.getY();

        //size update
        this.csize.set(
                PERCENT.HAS_SIZE_X(percentFlag) ? pW * w : (w >= 0 ? w : pW + w),
                PERCENT.HAS_SIZE_Y(percentFlag) ? pH * h : (h >= 0 ? h : pH + h)
        );

        //pos update
        this.cpos.set(
                pX + parentAnchor.origin.getX() * pW
                        + this.pos.getX() * (PERCENT.HAS_POS_X(percentFlag) ? pW : 1) * parentAnchor.multiplier.getX()
                        - anchor.origin.getX() * this.csize.getX(),
                pY + parentAnchor.origin.getY() * pH
                        + this.pos.getY() * (PERCENT.HAS_POS_Y(percentFlag) ? pH : 1) * parentAnchor.multiplier.getY()
                        - anchor.origin.getY() * this.csize.getY()
        );

        //return if no stretch limits
        if(minX == null && maxX == null && minY == null && maxY == null) {
            lastUpdated = System.nanoTime();
            return;
        }

        //calculate x stretch limits
        if (minX != null || maxX != null) {
            double oldPX = pX;
            if (minX != null) pX = minX.getDisplayedPos().getX() + minX.getDisplayedSize().getX();
            pW = maxX == null ? oldPX + pW - pX : maxX.getDisplayedPos().getX() - pX;
        }

        //calculate y stretch limits
        if (minY != null || maxY != null) {
            double oldPY = pY;
            if (minY != null) pY = minY.getDisplayedPos().getY() + minY.getDisplayedSize().getY();
            pH = maxY == null ? oldPY + pH - pY : maxY.getDisplayedPos().getY() - pY;
        }

        //update size again with new stretch limits
        this.csize.set(
                PERCENT.HAS_SIZE_X(percentFlag) ? pW * w : (w >= 0 ? w : pW + w),
                PERCENT.HAS_SIZE_Y(percentFlag) ? pH * h : (h >= 0 ? h : pH + h)
        );

        //update pos again with new stretch limits
        this.cpos.set(
                pX + parentAnchor.origin.getX() * pW
                        + this.pos.getX() * (PERCENT.HAS_POS_X(percentFlag) ? pW : 1) * parentAnchor.multiplier.getX()
                        - anchor.origin.getX() * this.csize.getX(),
                pY + parentAnchor.origin.getY() * pH
                        + this.pos.getY() * (PERCENT.HAS_POS_Y(percentFlag) ? pH : 1) * parentAnchor.multiplier.getY()
                        - anchor.origin.getY() * this.csize.getY()
        );

        lastUpdated = System.nanoTime();
    }

    protected Component rParent() {
        return absolute ? root : parent;
    }

    public Vector2D getDisplayedSize() {
        if (parent != null && parent.getLastUpdated() > lastUpdated || root != null && root.getLastUpdated() > lastUpdated)
            updatePosAndSize();
        return this.csize;
    }

    public void setSize(Vector2D size) {
        if (this.size.equals(size)) return;
        this.size.set(
                PERCENT.HAS_SIZE_X(percentFlag) ? MathUtil.constrain01(size.getX()) : size.getX(),
                PERCENT.HAS_SIZE_Y(percentFlag) ? MathUtil.constrain01(size.getY()) : size.getY()
        );
        updatePosAndSize();
    }

    public void setHeight(double h, boolean percent) {
        if (this.size.getY() == h && percent == PERCENT.HAS_SIZE_Y(percentFlag)) return;
        percentFlag = PERCENT.SET_SIZE_Y(percentFlag, percent);
        this.size.setY(PERCENT.HAS_SIZE_Y(percentFlag) ? MathUtil.constrain01(h) : h);
        updatePosAndSize();
    }

    public void setWidth(double w, boolean percent) {
        if (this.size.getX() == w && percent == PERCENT.HAS_SIZE_X(percentFlag)) return;
        percentFlag = PERCENT.SET_SIZE_X(percentFlag, percent);
        this.size.setX(PERCENT.HAS_SIZE_X(percentFlag) ? MathUtil.constrain01(w) : w);
        updatePosAndSize();
    }

    public void addSize(Vector2D offset) {
        Vector2D transformed = parentAnchor.transformVec(offset);
        if (rParent() != null) {
            if (PERCENT.HAS_SIZE_X(percentFlag))
                transformed.setX(transformed.getX() / rParent().getDisplayedSize().getX());
            if (PERCENT.HAS_SIZE_Y(percentFlag))
                transformed.setY(transformed.getY() / rParent().getDisplayedSize().getY());
        }
        this.setSize(this.size.add(transformed));
    }

    public void addPos(Vector2D offset) {
        Vector2D transformed = parentAnchor.transformVec(offset);
        if (rParent() != null) {
            if (PERCENT.HAS_POS_X(percentFlag))
                transformed.setX(transformed.getX() / rParent().getDisplayedSize().getX());
            if (PERCENT.HAS_POS_Y(percentFlag))
                transformed.setY(transformed.getY() / rParent().getDisplayedSize().getY());
        }
        this.setPos(this.pos.add(transformed));
    }

    public void setCPos(Vector2D pos) {
        this.cpos.set(pos);
    }

    public void setPos(Vector2D pos) {
        if (this.pos.equals(pos)) return;
        if (PERCENT.HAS_POS_X(percentFlag) && MathUtil.constrain01(pos.getX()) != pos.getX() ||
                PERCENT.HAS_POS_Y(percentFlag) && MathUtil.constrain01(pos.getY()) != pos.getY()) {
            Debug.stacktrace("Warning: position not in range 0 - 1 even though percent flag is true for this field");
        }
        this.pos.set(
                PERCENT.HAS_POS_X(percentFlag) ? MathUtil.constrain01(pos.getX()) : pos.getX(),
                PERCENT.HAS_POS_Y(percentFlag) ? MathUtil.constrain01(pos.getY()) : pos.getY()
        );
        updatePosAndSize();
    }

    public void setParent(Component parent) {
        if (this.parent == parent) return;

        this.parent = parent;
        this.root = (parent == null) ? this : parent.getRoot();
    }

    public Component setAbsolute(boolean absolute) {
        this.absolute = absolute;
        return this;
    }

    public Component setPercentFlag(int percentFlag) {
        this.percentFlag = percentFlag;
        return this;
    }

    public Component setThisAnchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }

    public Component setParentAnchor(Anchor parentAnchor) {
        this.parentAnchor = parentAnchor;
        return this;
    }

    public Component setAnchors(Anchor anchor) {
        return this
                .setThisAnchor(anchor)
                .setParentAnchor(anchor);
    }

    @SuppressWarnings("unused")
    public static class PERCENT {
        public static final int NONE = 0;
        public static final int POS_X = 1;
        public static final int POS_Y = 1 << 1;
        public static final int SIZE_X = 1 << 2;
        public static final int SIZE_Y = 1 << 3;
        public static final int ALL = POS_X | POS_Y | SIZE_X | SIZE_Y;
        public static final int POS = POS_X | POS_Y;
        public static final int SIZE = SIZE_X | SIZE_Y;
        public static final int X = POS_X | SIZE_X;
        public static final int Y = POS_Y | SIZE_Y;

        public static boolean HAS_POS_X(int flag) {
            return (flag & POS_X) != 0;
        }

        public static int SET_POS_X(int flag, boolean state) {
            flag &= ~POS_X;
            if (state) flag |= POS_X;
            return flag;
        }

        public static boolean HAS_POS_Y(int flag) {
            return (flag & POS_Y) != 0;
        }

        public static int SET_POS_Y(int flag, boolean state) {
            flag &= ~POS_Y;
            if (state) flag |= POS_Y;
            return flag;
        }

        public static boolean HAS_SIZE_X(int flag) {
            return (flag & SIZE_X) != 0;
        }

        public static int SET_SIZE_X(int flag, boolean state) {
            flag &= ~SIZE_X;
            if (state) flag |= SIZE_X;
            return flag;
        }

        public static boolean HAS_SIZE_Y(int flag) {
            return (flag & SIZE_Y) != 0;
        }

        public static int SET_SIZE_Y(int flag, boolean state) {
            flag &= ~SIZE_Y;
            if (state) flag |= SIZE_Y;
            return flag;
        }
    }

    public abstract void render(Vector2D mouse);
}
