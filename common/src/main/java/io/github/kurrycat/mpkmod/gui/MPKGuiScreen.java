package io.github.kurrycat.mpkmod.gui;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.Component;
import io.github.kurrycat.mpkmod.gui.components.ComponentHolder;
import io.github.kurrycat.mpkmod.gui.components.Pane;
import io.github.kurrycat.mpkmod.gui.components.PopupMenu;
import io.github.kurrycat.mpkmod.gui.interfaces.KeyInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseScrollListener;
import io.github.kurrycat.mpkmod.util.ItrUtil;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;
import java.util.List;

@SuppressWarnings("unused")
public abstract class MPKGuiScreen implements KeyInputListener, MouseInputListener, MouseScrollListener {
    private final GuiScreenRoot<Component> guiRoot = new GuiScreenRoot<>();
    private final GuiScreenRoot<Pane<?>> panesRoot = new GuiScreenRoot<>();

    private String id = null;
    private boolean initialized = false;

    public final String getID() {
        return id;
    }

    @SuppressWarnings("UnusedReturnValue")
    public final MPKGuiScreen setID(String id) {
        this.id = id;
        return this;
    }

    public Vector2D getScreenSize() {
        return Renderer2D.getScaledSize();
    }

    public GuiScreenRoot<Component> getGuiRoot() {
        return guiRoot;
    }

    public List<Pane<?>> getOpenPanes() {
        return panesRoot.getChildren();
    }

    // TODO: figure out a better way to do this (only used for events, probably won't stay)
    protected List<Component> getComponents() {
        return getGuiRoot().getChildren();
    }

    public final void onInit() {
        if (!initialized || resetOnOpen())
            onGuiInit();
        initialized = true;
    }

    public boolean resetOnOpen() {
        return true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void onGuiInit() {
        getGuiRoot().clearChildren();
    }
    public void onGuiClosed() {}
    public void onGuiResized(Vector2D screenSize) {}
    public final void onResize(int width, int height) {
        onGuiResized(getScreenSize());
    }

    /**
     * @param keyCode key that triggered the event
     * @param scanCode scan code of the key that triggered the event (hardware-level)
     * @param modifiers active key modifiers
     * @param isCharTyped whether the key pressed types a character
     * @return whether the event was consumed
     */
    public boolean onKeyEvent(int keyCode, int scanCode, int modifiers, boolean isCharTyped) {
        return handleKeyInput(keyCode, scanCode, modifiers, isCharTyped);
    }

    /**
     * @param mouse mouse position when clicked
     * @param mouseButton mouse button that was pressed
     * @return whether the event was consumed
     */
    public boolean onMouseClicked(Vector2D mouse, int mouseButton) {
        return handleMouseInput(Mouse.State.DOWN, mouse, Mouse.Button.fromInt(mouseButton));
    }

    /**
     * @param mouse mouse position
     * @param mouseButton mouse button that is held
     * @param timeSinceLastClick time elapsed since last click in nanoseconds
     * @return whether the event was consumed
     */
    public boolean onMouseClickMove(Vector2D mouse, int mouseButton, long timeSinceLastClick) {
        return handleMouseInput(Mouse.State.DRAG, mouse, Mouse.Button.fromInt(mouseButton));
    }

    /**
     * @param mouse mouse position when released
     * @param mouseButton mouse button that was released
     * @return whether the event was consumed
     */
    public boolean onMouseReleased(Vector2D mouse, int mouseButton) {
        return handleMouseInput(Mouse.State.UP, mouse, Mouse.Button.fromInt(mouseButton));
    }

    /**
     * @param mouse mouse position when scrolled
     * @param delta number of lines to scroll (one scroll tick = 3 per default)<br>
     *                delta {@literal <} 0: scrolled down<br>
     *                delta {@literal >} 0: scrolled up
     * @return whether the event was consumed
     */
    public boolean onMouseScroll(Vector2D mouse, int delta) {
        return handleMouseScroll(mouse, delta);
    }

    @Override
    public boolean handleKeyInput(int keyCode, int scanCode, int modifiers, boolean isCharTyped) {
        if (!getOpenPanes().isEmpty())
            getOpenPanes().get(getOpenPanes().size() - 1).handleKeyInput(keyCode, scanCode, modifiers, isCharTyped);
        return ItrUtil.orMap(
                ItrUtil.getAllOfType(KeyInputListener.class, getComponents()),
                b -> b.handleKeyInput(keyCode, scanCode, modifiers, isCharTyped)
        );
    }

    @Override
    public boolean handleMouseInput(Mouse.State state, Vector2D mousePos, Mouse.Button button) {
        if (!getOpenPanes().isEmpty()) {
            Pane<?> topPane = getOpenPanes().get(getOpenPanes().size() - 1);
            topPane.handleMouseInput(state, mousePos, button);
            if (topPane.isLoaded()) return true;
        }
        return ItrUtil.orMap(
                ItrUtil.getAllOfType(MouseInputListener.class, getComponents()),
                b -> b.handleMouseInput(state, mousePos, button)
        );
    }

    @Override
    public boolean handleMouseScroll(Vector2D mousePos, int delta) {
        if (!getOpenPanes().isEmpty())
            getOpenPanes().get(getOpenPanes().size() - 1).handleMouseScroll(mousePos, delta);
        return ItrUtil.orMap(
                ItrUtil.getAllOfType(MouseScrollListener.class, getComponents()),
                b -> b.handleMouseScroll(mousePos, delta)
        );
    }

    public final void drawScreen(Vector2D mouse, float partialTicks) {
        if (getOpenPanes().isEmpty() || getOpenPanes().get(getOpenPanes().size() - 1) instanceof PopupMenu) drawDefaultBackground();
        Vector2D hoverMousePos = getOpenPanes().isEmpty() ? mouse : new Vector2D(-1, -1);

        renderScreen(hoverMousePos, partialTicks);

        if (!getOpenPanes().isEmpty()) {
            Pane<?> last = getOpenPanes().get(getOpenPanes().size() - 1);
            if (!(last instanceof PopupMenu))
                drawDefaultBackground();
            for (int i = 0; i < getOpenPanes().size() - 1; i++) {
                getOpenPanes().get(i).render(Vector2D.OFFSCREEN);
            }
            last.render(mouse);
        }

        Renderer2D.endFrame();
    }

    public void renderScreen(Vector2D mouse, float partialTicks) {
        getGuiRoot().render(mouse);
    }

    public final void drawDefaultBackground() {
        Renderer2D.drawRect(Vector2D.ZERO, getScreenSize().add(2), new Color(16, 16, 16, 140));
    }

    public boolean shouldCreateKeyBind() {
        return false;
    }

    public void onKeybindPressed() {
        Minecraft.displayGuiScreen(this);
    }

    public final void close() {
        Minecraft.displayGuiScreen(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends MPKGuiScreen> void openPane(Pane<T> p) {
        panesRoot.addChild(p);
        p.setScreen((T) this);
        p.setLoaded(true);
    }

    public <T extends MPKGuiScreen> void closePane(Pane<T> p) {
        panesRoot.removeChild(p);
        p.setLoaded(false);
    }

    public <T extends MPKGuiScreen> void openPane(Pane<T> p, Vector2D pos) {
        openPane(p);
        p.setPos(pos);
    }

    public final void closeAllPanes() {
        for (int i = getOpenPanes().size() - 1; i >= 0; i--) {
            getOpenPanes().get(i).close();
        }
    }

    public class GuiScreenRoot<C extends Component> extends ComponentHolder<C> {
        public MPKGuiScreen getScreen() {
            return MPKGuiScreen.this;
        }

        @Override
        public long getLastUpdated() {
            return Long.MAX_VALUE;
        }  // TODO: replace lastUpdated with dirty flag

        @Override
        public Vector2D getDisplayedPos() {
            return Vector2D.ZERO;
        }

        @Override
        public Vector2D getDisplayedSize() {
            return getScreenSize();
        }

        @Override
        public ComponentHolder<C> getRoot() {
            return this;
        }
    }
}
