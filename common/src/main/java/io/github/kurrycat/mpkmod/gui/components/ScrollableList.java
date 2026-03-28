package io.github.kurrycat.mpkmod.gui.components;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.Theme;
import io.github.kurrycat.mpkmod.gui.interfaces.HoverComponent;
import io.github.kurrycat.mpkmod.gui.interfaces.KeyInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseScrollListener;
import io.github.kurrycat.mpkmod.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScrollableList<I extends ScrollableListItem<I>> extends Component implements MouseInputListener, MouseScrollListener, KeyInputListener, HoverComponent {
    private final TextRectangle titleComponent;
    public Color backgroundColor = Theme.darkBackground;
    public Color edgeColor = Theme.darkEdge;
    public ScrollBar scrollBar;
    public Div topCover;
    public Div bottomCover;
    public ItemHolder content;

    public ScrollableList() {
        topCover = new Div(new Vector2D(0, 0), new Vector2D(1, 0))
                .setPercentFlag(PERCENT.SIZE_X)
                .setAnchors(Anchor.TOP_LEFT);
        topCover.backgroundColor = backgroundColor;
        addChild(topCover);

        titleComponent = new TextRectangle(
                new Vector2D(0, 0),
                new Vector2D(1, 1),
                "", null, Color.WHITE
        ).setPercentFlag(PERCENT.SIZE);
        topCover.addChild(titleComponent);

        bottomCover = new Div(new Vector2D(0, 0), new Vector2D(1, 0))
                .setPercentFlag(PERCENT.SIZE_X)
                .setAnchors(Anchor.BOTTOM_LEFT);
        bottomCover.backgroundColor = backgroundColor;
        addChild(bottomCover);

        content = new Div(new Vector2D(0, 0), new Vector2D(1, 1), true)
                .setPercentFlag(PERCENT.SIZE);
        addChild(content);
        stretchYBetween(content, topCover, bottomCover);

        scrollBar = new ScrollBar()
                .setPercentFlag(PERCENT.SIZE_Y)
                .setAnchors(Anchor.TOP_RIGHT);
        content.addChild(scrollBar);
        scrollBar.setSize(new Vector2D(scrollBar.barWidth, 1));
    }

    public void addItem(I item) {
        this.items.add(item);
        content.addChild(item);
    }

    public void removeItem(I item) {
        this.items.remove(item);
        content.removeChild(item);
    }

    public void clearItems() {
        while (!this.items.isEmpty())
            removeItem(this.items.get(0));
    }

    public void setTitle(String title) {
        this.titleComponent.setText(Colors.UNDERLINE + title);
        if (topCover.getDisplayedSize().getY() < 20)
            topCover.setSize(new Vector2D(topCover.size.getX(), 20));
    }

    @Override
    protected void postLayout() {
        super.postLayout();

        int relItemYPos = 1;
        double itemWidth = getDisplayedSize().getX() - 2;
        if (shouldRenderScrollbar()) itemWidth -= scrollBar.barWidth - 1;

        for (I item : getItems()) {
            double absItemYPos = relItemYPos - scrollBar.scrollAmount;
            item.setPos(new Vector2D(1, absItemYPos));
            item.setSize(new Vector2D(itemWidth, item.getHeight()));

            relItemYPos += item.getHeight() + 1;
        }
    }

    @Override
    public void render(Vector2D mouse) {
        Renderer2D.drawHollowRect(getDisplayedPos().add(1), getDisplayedSize().sub(2), 1, edgeColor);
        if (topCover.getDisplayedSize().getY() > 0)
            Renderer2D.drawRect(topCover.getDisplayedPos().add(0, topCover.getDisplayedSize().getY()),
                    new Vector2D(topCover.getDisplayedSize().getX(), 1), edgeColor);
        if (bottomCover.getDisplayedSize().getY() > 0)
            Renderer2D.drawRect(bottomCover.getDisplayedPos(), new Vector2D(bottomCover.getDisplayedSize().getX(), 1), edgeColor);
        if (shouldRenderScrollbar())
            scrollBar.render(mouse);

        super.render(mouse);
    }

    private boolean shouldRenderScrollbar() {
        return totalHeight() > content.getDisplayedSize().getY() - 2;
    }

    /**
     * Override this if you don't want to use the default ArrayList implementation
     *
     * @return an iterable containing the items
     */
    public Iterable<I> getItems() {
        return items;
    }

    public int totalHeight() {
        int sum = 0;
        for (I item : getItems()) sum += item.getHeight() + 1;
        if (sum != 0) sum += 3;

        return sum;
    }

    public Pair<I, Vector2D> getItemAndRelMousePosUnderMouse(Vector2D mouse) {
        double itemWidth = content.getDisplayedSize().getX() - 2;
        if (shouldRenderScrollbar()) itemWidth -= scrollBar.barWidth - 1;
        if (mouse.getX() < content.getDisplayedPos().getX() + 1 || mouse.getX() > content.getDisplayedPos().getX() + itemWidth + 1)
            return null;

        double currY = mouse.getY() - 1 - content.getDisplayedPos().getY() + scrollBar.scrollAmount;
        for (I item : getItems()) {
            if (currY >= 0 && currY <= item.getHeight()) {
                return new Pair<>(item, new Vector2D(mouse.getX() - content.getDisplayedPos().getX() - 1, currY));
            }
            currY -= item.getHeight() + 1;
        }
        return null;
    }

    @Override
    public boolean handleMouseInput(Mouse.State state, Vector2D mousePos, Mouse.Button button) {
        if (shouldRenderScrollbar() && scrollBar.handleMouseInput(state, mousePos, button))
            return true;

        boolean itemClicked = false;
        if (mousePos.getY() > content.getDisplayedPos().getY() &&
                mousePos.getY() < content.getDisplayedPos().getY() + content.getDisplayedSize().getY())
            for (I item : getItems()) {
                if (item.isVisible()) {
                    itemClicked = itemClicked || item.handleMouseInput(state, mousePos, button);
                }
            }

        return itemClicked ||
                ItrUtil.orMapAll(
                        ItrUtil.getAllOfType(MouseInputListener.class, children, topCover.children, bottomCover.children),
                        e -> e.handleMouseInput(state, mousePos, button)
                ) || contains(mousePos);
    }

    @Override
    public boolean handleMouseScroll(Vector2D mousePos, int delta) {
        if (!contains(mousePos)) return false;

        boolean itemClicked = false;
        if (mousePos.getY() > content.getDisplayedPos().getY() &&
                mousePos.getY() < content.getDisplayedPos().getY() + content.getDisplayedSize().getY())
            for (I item : getItems()) {
                if (item.isVisible()) {
                    itemClicked = itemClicked || item.handleMouseScroll(mousePos, delta);
                }
            }

        if (itemClicked ||
                ItrUtil.orMapAll(
                        ItrUtil.getAllOfType(MouseScrollListener.class, children, topCover.children, bottomCover.children),
                        e -> e.handleMouseScroll(mousePos, delta)
                )
        ) return true;

        if (shouldRenderScrollbar())
            scrollBar.scrollBy(-delta);
        return contains(mousePos);
    }

    @Override
    public boolean handleKeyInput(int keyCode, int scanCode, int modifiers, boolean isCharTyped) {
        boolean itemClicked = false;
        for (I item : getItems()) {
            if (item.isVisible()) {
                itemClicked = itemClicked || item.handleKeyInput(keyCode, scanCode, modifiers, isCharTyped);
            }
        }
        return itemClicked ||
                ItrUtil.orMapAll(
                        ItrUtil.getAllOfType(KeyInputListener.class, children, topCover.children, bottomCover.children),
                        e -> e.handleKeyInput(keyCode, scanCode, modifiers, isCharTyped)
                );
    }

    @Override
    public void renderHover(Vector2D mouse) {
        getItems().forEach(i -> i.renderHover(mouse));
        ItrUtil.getAllOfType(HoverComponent.class, children, topCover.children, bottomCover.children)
                .forEach(i -> i.renderHover(mouse));
    }

    public class ItemHolder extends Component {
        @Override
        protected void updatePosAndSize() {
            super.updatePosAndSize();


        }
    }

    public class ScrollBar extends Component implements MouseInputListener {
        public double barWidth = 11;
        public Color backgroundColor = Color.DARK_GRAY;
        public Color hoverColor = new Color(180, 180, 180);
        public Color clickedColor = new Color(101, 101, 101);
        private int scrollAmount = 0;

        private int clickedYOffset = -1;

        @Override
        public void render(Vector2D mouse) {
            Renderer2D.drawRectWithEdge(getDisplayedPos(), getDisplayedSize(), 1, backgroundColor, Color.BLACK);
            BoundingBox2D scrollButtonBB = getScrollButtonBB();

            Renderer2D.drawRect(
                    scrollButtonBB.getMin().add(1),
                    scrollButtonBB.getSize().sub(2),
                    clickedYOffset != -1 ? clickedColor : contains(mouse) ? hoverColor : Color.WHITE
            );
        }

        public BoundingBox2D getScrollButtonBB() {
            return BoundingBox2D.fromPosSize(
                    new Vector2D(
                            getDisplayedPos().getX() + 1,
                            getDisplayedPos().getY() + mapScrollAmountToScrollButtonPos()
                    ),
                    new Vector2D(barWidth - 2, getScrollButtonHeight())
            );
        }

        public int mapScrollAmountToScrollButtonPos() {
            return MathUtil.map(
                    scrollAmount,
                    0, ScrollableList.this.totalHeight() - getDisplayedSize().getYI() - 2,
                    1, getDisplayedSize().getYI() - getScrollButtonHeight() - 1
            );
        }

        public int getScrollButtonHeight() {
            int totalHeight = ScrollableList.this.totalHeight();
            if (totalHeight == 0) totalHeight++;
            return Math.min(MathUtil.sqr(getDisplayedSize().getYI() - 2) / totalHeight, getDisplayedSize().getYI() - 2);
        }

        @Override
        public boolean handleMouseInput(Mouse.State state, Vector2D mousePos, Mouse.Button button) {
            switch (state) {
                case DOWN:
                    if (getScrollButtonBB().contains(mousePos))
                        clickedYOffset = mousePos.getYI() - getScrollButtonBB().getMin().getYI();
                    break;
                case DRAG:
                    if (clickedYOffset != -1)
                        setScrollAmount(mapScrollButtonPosToScrollAmount(mousePos));
                    break;
                case UP:
                    if (clickedYOffset != -1)
                        setScrollAmount(mapScrollButtonPosToScrollAmount(mousePos));
                    clickedYOffset = -1;
                    break;
            }

            return getScrollButtonBB().contains(mousePos);
        }

        public int mapScrollButtonPosToScrollAmount(Vector2D pos) {
            return MathUtil.map(
                    pos.getYI() - clickedYOffset - getDisplayedPos().getYI(),
                    1, getDisplayedSize().getYI() - getScrollButtonHeight() - 1,
                    0, ScrollableList.this.totalHeight() - getDisplayedSize().getYI() - 2
            );
        }

        private void constrainScrollAmountToScreen() {
            scrollAmount = MathUtil.constrain(scrollAmount, 0, ScrollableList.this.totalHeight() - getDisplayedSize().getYI() - 2);
        }

        public void scrollBy(int delta) {
            setScrollAmount(scrollAmount + delta);
        }

        public void setScrollAmount(int scrollAmount) {
            this.scrollAmount = scrollAmount;
            constrainScrollAmountToScreen();

            markDirty();
        }
    }
}
