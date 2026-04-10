package io.github.kurrycat.mpkmod.gui;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.Component;
import io.github.kurrycat.mpkmod.gui.components.Container;
import io.github.kurrycat.mpkmod.gui.components.Pane;
import io.github.kurrycat.mpkmod.gui.components.PaneHolder;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;
import java.util.ArrayList;


@SuppressWarnings("unused")
public abstract class MPKGuiScreen implements PaneHolder {
    private final Container guiRoot = new GuiScreenRoot();
    public ArrayList<Pane<?>> openPanes = new ArrayList<>();

    private boolean initialized = false;
    private String id = null;

    public Container getGuiRoot() {
        return guiRoot;
    }

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


    public void addGuiComponent(Component component) {
        getGuiRoot().addChild(component);
    }

    public void removeGuiComponent(Component component) {
        getGuiRoot().removeChild(component);
    }


    public final void onInit() {
        if (!initialized || resetOnOpen())
            onGuiInit();
        initialized = true;
    }

    public final void onResize(int width, int height) {
        onGuiResized(getScreenSize());
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


    public final void drawScreen(Vector2D mouse, float partialTicks) {
        render(mouse, partialTicks);
        Renderer2D.endFrame();
    }

    public void render(Vector2D mouse, float partialTicks) {
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


    public void onKeyEvent(int keyCode, int scanCode, int modifiers, boolean pressed) {}

    public void onMouseClicked(Vector2D mouse, int mouseButton) {}

    public void onMouseClickMove(Vector2D mouse, int mouseButton, long timeSinceLastClick) {}

    public void onMouseReleased(Vector2D mouse, int mouseButton) {}

    /**
     * @param mousePos Mouse position when scrolled
     * @param delta    number of lines to scroll (one scroll tick = 3 per default)<br>
     *                 delta {@literal <} 0: scrolled down<br>
     *                 delta {@literal >} 0: scrolled up
     */
    public void onMouseScroll(Vector2D mousePos, int delta) {}

    @Override
    public <T extends PaneHolder> void openPane(Pane<T> p, Vector2D pos) {
        p.setPos(pos);
        openPane(p);
    }

    @SuppressWarnings("unchecked")
    public <T extends PaneHolder> void openPane(Pane<T> p) {
        openPanes.add(p);
        p.setPaneHolder((T) this);
        p.setLoaded(true);
    }

    @Override
    public <T extends PaneHolder> void closePane(Pane<T> p) {
        openPanes.remove(p);
        p.setLoaded(false);
    }

    public final void closeAllPanes() {
        for (int i = openPanes.size() - 1; i >= 0; i--) {
            openPanes.get(i).close();
        }
    }


    public class GuiScreenRoot extends Container {
        @Override
        public Vector2D getDisplayedPos() {
            return Vector2D.ZERO;
        }

        @Override
        public Vector2D getDisplayedSize() {
            return getScreenSize();
        }

        @Override
        public void setPos(Vector2D pos) {
            throw new UnsupportedOperationException("Root cannot have its position set");
        }

        @Override
        public void setSize(Vector2D size) {
            throw new UnsupportedOperationException("Root cannot have its size set");
        }
    }
}
