package io.github.kurrycat.mpkmod.gui;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.components.Component;
import io.github.kurrycat.mpkmod.gui.components.ComponentHolder;
import io.github.kurrycat.mpkmod.gui.components.Pane;
import io.github.kurrycat.mpkmod.gui.components.PaneHolder;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class MPKGuiScreen extends ComponentHolder implements PaneHolder {
    public ArrayList<Pane<?>> openPanes = new ArrayList<>();

    private boolean initialized = false;
    private String id = null;

    public final String getID() {
        return id;
    }

    @SuppressWarnings("UnusedReturnValue")
    public final MPKGuiScreen setID(String id) {
        this.id = id;
        return this;
    }

    public Vector2D getScreenSize() {
        return getDisplayedSize();
    }

    public final void onInit() {
        setSize(Renderer2D.getScaledSize());
        setRoot(this);
        if (!initialized || resetOnOpen())
            onGuiInit();
        initialized = true;
    }

    public boolean resetOnOpen() {
        return true;
    }

    public void onGuiInit() {
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void onGuiClosed() {
    }

    public final void onResize(int width, int height) {
        setSize(new Vector2D(width, height));
        onGuiResized(size);
    }

    public void onGuiResized(Vector2D screenSize) {
    }

    public void onKeyEvent(int keyCode, int scanCode, int modifiers, boolean pressed) {
    }

    public void onMouseClicked(Vector2D mouse, int mouseButton) {
    }

    public void onMouseClickMove(Vector2D mouse, int mouseButton, long timeSinceLastClick) {
    }

    public void onMouseReleased(Vector2D mouse, int mouseButton) {
    }

    /**
     * @param mousePos Mouse position when scrolled
     * @param delta    number of lines to scroll (one scroll tick = 3 per default)<br>
     *                 delta {@literal <} 0: scrolled down<br>
     *                 delta {@literal >} 0: scrolled up
     */
    public void onMouseScroll(Vector2D mousePos, int delta) {
    }

    public final void drawScreen(Vector2D mouse, float partialTicks) {
        render(mouse, partialTicks);
        Renderer2D.endFrame();
    }

    public void render(Vector2D mouse, float partialTicks) {
        for (Component c : components) c.render(mouse);
    }

    public final void drawDefaultBackground() {
        Renderer2D.drawRect(Vector2D.ZERO, size.add(2), new Color(16, 16, 16, 140));
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

    @Override
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
}
