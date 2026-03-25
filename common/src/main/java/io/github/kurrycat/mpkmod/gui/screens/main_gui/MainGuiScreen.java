package io.github.kurrycat.mpkmod.gui.screens.main_gui;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.InputConstants;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.MPKGuiScreen;
import io.github.kurrycat.mpkmod.gui.components.*;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.Component.PERCENT;
import io.github.kurrycat.mpkmod.gui.components.PopupMenu;
import io.github.kurrycat.mpkmod.gui.interfaces.KeyInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseInputListener;
import io.github.kurrycat.mpkmod.gui.interfaces.MouseScrollListener;
import io.github.kurrycat.mpkmod.util.BoundingBox2D;
import io.github.kurrycat.mpkmod.util.ItrUtil;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MainGuiScreen extends MPKGuiScreen implements MessageReceiver {
    private final GuiScreenRoot<HudComponent> hudRoot = new GuiScreenRoot<>();

    public OptionsPane optionsPane = null;
    public LoadConfigPane loadConfigPane = null;
    public SaveConfigPane saveConfigPane = null;
    public Set<HudComponent> selected = new HashSet<>();
    public Set<HudComponent> holding = new HashSet<>();
    public Set<HudComponent> highlighted = new HashSet<>();
    private Vector2D lastClickedPos = null;
    private HudComponent lastClicked = null;
    private Vector2D holdingSetPosOffset = null;

    public MainGuiScreen() {
        super();
        addRoot(hudRoot);
    }

    public GuiScreenRoot<HudComponent> getHudRoot() {
        return hudRoot;
    }

    public void renderHud() {
        getHudRoot().updateTree();
        getHudRoot().layoutTree();
        getHudRoot().render(Vector2D.OFFSCREEN);
    }

    @Override
    public boolean shouldCreateKeyBind() {
        return true;
    }

    @Override
    public void onGuiInit() {
        super.onGuiInit();
        getHudRoot().clearChildren();

        selected.clear();
        holding.clear();
        highlighted.clear();
        lastClicked = null;
        lastClickedPos = null;
        holdingSetPosOffset = null;

        reloadConfig();

        getGuiRoot().addChild(
                new Button(
                        "Save",
                        new Vector2D(115, 5),
                        new Vector2D(50, 20),
                        mouseButton -> MainGuiScreen.this.openPane(saveConfigPane)
                ).setAnchors(Anchor.BOTTOM_RIGHT)
        );

        getGuiRoot().addChild(
                new Button(
                        "Load File",
                        new Vector2D(60, 5),
                        new Vector2D(50, 20),
                        mouseButton -> MainGuiScreen.this.openPane(loadConfigPane)
                ).setAnchors(Anchor.BOTTOM_RIGHT)
        );

        getGuiRoot().addChild(
                new Button(
                        "Options",
                        new Vector2D(5, 5),
                        new Vector2D(50, 20),
                        mouseButton -> MainGuiScreen.this.openPane(optionsPane)
                ).setAnchors(Anchor.BOTTOM_RIGHT)
        );

        optionsPane = new OptionsPane(Vector2D.ZERO, new Vector2D(3 / 5D, 3 / 5D))
                .setPercentFlag(PERCENT.ALL)
                .setAnchors(Anchor.CENTER);

        loadConfigPane = new LoadConfigPane(Vector2D.ZERO, new Vector2D(3 / 5D, 1))
                .setPercentFlag(PERCENT.ALL)
                .setAnchors(Anchor.CENTER);

        saveConfigPane = new SaveConfigPane(Vector2D.ZERO, new Vector2D(3 / 5D, 1))
                .setPercentFlag(PERCENT.ALL)
                .setAnchors(Anchor.CENTER);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        getHudRoot().getChildren().forEach(c -> c.setSelected(false));
        getHudRoot().getChildren().forEach(c -> c.setHighlighted(false));
        selected.clear();
        holding.clear();
        highlighted.clear();
        closeAllPanes();
        LabelConfiguration.currentConfig.saveInCustom();
    }

    private void cleanupScreen() {
        selected.clear();
        holding.clear();
        lastClicked = null;
        lastClickedPos = null;
        holdingSetPosOffset = null;
    }

    @Override
    public void renderScreen(Vector2D mouse, float partialTicks) {
        getHudRoot().getChildren().forEach(c -> c.setSelected(selected.contains(c)));
        getHudRoot().getChildren().forEach(c -> c.setHighlighted(highlighted.contains(c)));

        for (HudComponent component : getHudRoot().getChildren()) {
            if (holding.contains(component)) {
                Vector2D offset = component.getRenderOffset();
                component.setRenderOffset(Vector2D.ZERO);
                component.render(mouse);
                component.setRenderOffset(offset);
            } else component.render(mouse);
        }

        super.renderScreen(mouse, partialTicks);

        if (!holding.isEmpty()) {
            BoundingBox2D containingHolding = boundingBoxContainingAll(new ArrayList<>(holding));

            Vector2D toMove = mouse.sub(lastClickedPos);
            toMove = toMove.constrain(
                    containingHolding.getMin().mult(-1),
                    getScreenSize().sub(containingHolding.getMax())
            );
            holdingSetPosOffset = toMove;
            for (HudComponent component : holding) {
                component.setRenderOffset(toMove);
                component.render(mouse);
            }
        }

        if (lastClickedPos != null && lastClicked == null && !mouse.equals(lastClickedPos)) {
            Vector2D p = new Vector2D(Math.min(lastClickedPos.getX(), mouse.getX()), Math.min(lastClickedPos.getY(), mouse.getY()));
            Vector2D s = new Vector2D(Math.max(lastClickedPos.getX(), mouse.getX()), Math.max(lastClickedPos.getY(), mouse.getY())).sub(p);
            Renderer2D.drawHollowRect(p, s, 1, Color.RED);
        }
    }

    public void addHudComponent(HudComponent c) {
        LabelConfiguration.currentConfig.components.add(c);
        reloadConfig();
    }

    public void removeHudComponent(HudComponent c) {
        LabelConfiguration.currentConfig.components.remove(c);
        reloadConfig();
    }

    public void reloadConfig() {
        getHudRoot().clearChildren();
        LabelConfiguration.currentConfig.components.forEach(getHudRoot()::addChild);
    }

    @Override
    public <T extends MPKGuiScreen> void openPane(Pane<T> p) {
        super.openPane(p);
        cleanupScreen();
    }

    @Override
    public <T extends MPKGuiScreen> void closePane(Pane<T> p) {
        super.closePane(p);

        if (getOpenPanes().isEmpty())
            highlighted.clear();
    }

    @Override
    public void postMessage(String receiverID, String content, boolean highlighted) {
        MessageQueue q = MessageQueue.getReceiverFor(receiverID, ItrUtil.getAllOfType(MessageQueue.class, getHudRoot().getChildren()));
        if (q != null)
            q.postMessage(content, highlighted);
    }

    public ArrayList<HudComponent> overlap(Vector2D p1, Vector2D p2) {
        return getHudRoot().getChildren().stream().filter(
                c -> {
                    Vector2D c1 = c.getDisplayedPos();
                    Vector2D c2 = c.getDisplayedPos().add(c.getDisplayedSize());

                    if (c1.getX() > p2.getX() || c2.getX() < p1.getX()) return false;
                    //noinspection RedundantIfStatement
                    if (c1.getY() > p2.getY() || c2.getY() < p1.getY()) return false;
                    return true;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    public HudComponent findFirstContainPos(Vector2D p) {
        ArrayList<HudComponent> containPos = findContainPos(p);
        if (containPos.isEmpty()) return null;
        return containPos.get(0);
    }

    public ArrayList<HudComponent> findContainPos(Vector2D p) {
        return getHudRoot().getChildren().stream().filter(c -> c.contains(p)).collect(Collectors.toCollection(ArrayList::new));
    }

    public BoundingBox2D boundingBoxContainingAll(ArrayList<HudComponent> components) {
        if (components.isEmpty()) return null;

        Vector2D min = null, max = null;
        for (HudComponent c : components) {
            Vector2D p = c.getDisplayedPos().sub(c.getRenderOffset());
            Vector2D p2 = p.add(c.getDisplayedSize());
            if (min == null) min = new Vector2D(p);
            if (max == null) max = new Vector2D(p.add(c.getDisplayedSize()));

            if (p.getX() < min.getX()) min.setX(p.getX());
            if (p2.getX() > max.getX()) max.setX(p2.getX());
            if (p.getY() < min.getY()) min.setY(p.getY());
            if (p2.getY() > max.getY()) max.setY(p2.getY());
        }
        return new BoundingBox2D(min, max);
    }

    @Override
    public boolean onKeyEvent(int keyCode, int scanCode, int modifiers, boolean isCharTyped) {
        if (super.onKeyEvent(keyCode, keyCode, modifiers, isCharTyped)) return true;

        if (!isCharTyped && !selected.isEmpty()) {
            Vector2D arrowKeyMove = Vector2D.ZERO;
            switch (keyCode) {
                case InputConstants.KEY_LEFT:
                    arrowKeyMove = Vector2D.LEFT;
                    break;
                case InputConstants.KEY_RIGHT:
                    arrowKeyMove = Vector2D.RIGHT;
                    break;
                case InputConstants.KEY_UP:
                    arrowKeyMove = Vector2D.UP;
                    break;
                case InputConstants.KEY_DOWN:
                    arrowKeyMove = Vector2D.DOWN;
                    break;
            }
            BoundingBox2D containingSelected = boundingBoxContainingAll(new ArrayList<>(selected));
            Vector2D toMove = arrowKeyMove.constrain(
                    Vector2D.ZERO.sub(containingSelected.getMin()),
                    getScreenSize().sub(containingSelected.getMax())
            );

            selected.forEach(c -> c.addPos(toMove));
        }

        return true;
    }

    @Override
    public boolean onMouseClicked(Vector2D mouse, int mouseButton) {
        if (super.onMouseClicked(mouse, mouseButton)) return true;

        if (getHudRoot().getChildren().isEmpty()) return true;

        if (Mouse.Button.LEFT.equals(mouseButton)) {
            highlighted.clear();
            lastClickedPos = mouse;

            HudComponent clicked = findFirstContainPos(lastClickedPos);
            lastClicked = clicked;

            if (clicked != null) {
                holding.clear();
                if (selected.contains(clicked))
                    holding.addAll(selected);
                selected.add(clicked);
                holding.add(clicked);
            }
        } else if (Mouse.Button.RIGHT.equals(mouseButton)) {
            highlighted.clear();
            if (lastClickedPos != null && lastClicked == null) {
                lastClickedPos = null;
            }
            HudComponent clicked = findFirstContainPos(mouse);
            if (selected.size() <= 1 && clicked != null) {
                highlighted.add(clicked);
                PopupMenu menu = clicked.getPopupMenu();
                if (menu != null) {
                    Vector2D windowSize = getScreenSize();
                    Vector2D cPos = clicked.getDisplayedPos();
                    Vector2D cSize = clicked.getDisplayedSize();
                    openPane(menu,
                            new Vector2D(
                                    cPos.getX() + cSize.getX() + menu.getDisplayedSize().getX() + 1 < windowSize.getX() ?
                                            cPos.getX() + cSize.getX() + 1 : cPos.getX() - menu.getDisplayedSize().getX() - 1,
                                    clicked.getDisplayedPos().getY()
                            )
                    );
                }
            } else if (!selected.isEmpty()) {
                highlighted.addAll(selected);
                selected.clear();
                PopupMenu menu = new PopupMenu();
                menu.addComponent(new Button("Delete", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    for (HudComponent c : highlighted)
                        menu.screen.removeHudComponent(c);
                    menu.close();
                }));
                openPane(menu, mouse);
            } else {
                highlighted.clear();
                PopupMenu menu = new PopupMenu();
                PopupMenu newLabelMenu = new PopupMenu();
                newLabelMenu.addComponent(new Button("Add InfoLabel", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    InfoLabel infoLabel = new InfoLabel("Example Label");
                    infoLabel.setPos(mouse);
                    addHudComponent(infoLabel);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add KeyBindingLabel", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    KeyBindingLabel keyBindingLabel = new KeyBindingLabel(mouse, new Vector2D(20, 20), "key.forward");
                    addHudComponent(keyBindingLabel);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add MessageQueue", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    MessageQueue messageQueue = new MessageQueue("Example MessageQueue");
                    messageQueue.setPos(mouse);
                    messageQueue.setSize(new Vector2D(30, 22));
                    addHudComponent(messageQueue);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add BarrierDisplay", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    BarrierDisplayComponent barrierDisplay = new BarrierDisplayComponent();
                    barrierDisplay.setPos(mouse);
                    barrierDisplay.setSize(new Vector2D(30, 30));
                    addHudComponent(barrierDisplay);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add InputHistory", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    InputHistory inputHistory = new InputHistory();
                    inputHistory.setPos(mouse);
                    inputHistory.setSize(new Vector2D(InputHistory.preferredWidth, 120));
                    addHudComponent(inputHistory);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add Plot (WIP)", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    Plot plot = new Last45Plot();
                    plot.setPos(mouse);
                    plot.setSize(new Vector2D(40, 40));
                    addHudComponent(plot);
                    menu.close();
                }));
                newLabelMenu.addComponent(new Button("Add Angle path (WIP)", b -> {
                    if (b != Mouse.Button.LEFT) return;
                    AnglePath path = new AnglePath(false);
                    path.setPos(mouse);
                    path.setSize(new Vector2D(40, 40));
                    addHudComponent(path);
                    menu.close();
                }));

                menu.addSubMenu(new Button("Add Label"), newLabelMenu);
                openPane(menu, mouse);
            }
        }

        return true;
    }

    @Override
    public boolean onMouseClickMove(Vector2D mouse, int mouseButton, long timeSinceLastClick) {
        if (super.onMouseClickMove(mouse, mouseButton, timeSinceLastClick)) return true;

        if (getHudRoot().getChildren().isEmpty()) return true;

        selected = selected.stream().filter(c -> holding.contains(c)).collect(Collectors.toCollection(HashSet::new));

        return true;
    }

    @Override
    public boolean onMouseReleased(Vector2D mouse, int mouseButton) {
        if (super.onMouseReleased(mouse, mouseButton)) return true;

        if (getHudRoot().getChildren().isEmpty()) return true;

        if (Mouse.Button.LEFT.equals(mouseButton) && lastClickedPos != null) {
            boolean moved = lastClickedPos.sub(mouse).lengthSqr() > 3 * 3;
            if (!moved && lastClicked != null) {
                selected.clear();
                selected.add(lastClicked);
            }

            if (holdingSetPosOffset != null) {
                for (HudComponent c : holding) {
                    c.setRenderOffset(Vector2D.ZERO);
                    c.addPos(holdingSetPosOffset);
                }
            }
            holding.clear();
            holdingSetPosOffset = null;

            if (lastClickedPos != null && lastClicked == null) {
                selected.clear();
                selected.addAll(overlap(
                        new Vector2D(Math.min(lastClickedPos.getX(), mouse.getX()), Math.min(lastClickedPos.getY(), mouse.getY())),
                        new Vector2D(Math.max(lastClickedPos.getX(), mouse.getX()), Math.max(lastClickedPos.getY(), mouse.getY()))
                ));
            }
            lastClickedPos = null;
            lastClicked = null;
        }

        return true;
    }

    @Override
    public boolean handleKeyInput(int keyCode, int scanCode, int modifiers, boolean isCharTyped) {
        if (!getOpenPanes().isEmpty())
            getOpenPanes().get(getOpenPanes().size() - 1).handleKeyInput(keyCode, scanCode, modifiers, isCharTyped);
        return ItrUtil.orMap(
                ItrUtil.getAllOfType(KeyInputListener.class, getGuiRoot().getChildren(), getHudRoot().getChildren()),
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
                ItrUtil.getAllOfType(MouseInputListener.class, getGuiRoot().getChildren(), getHudRoot().getChildren()),
                b -> b.handleMouseInput(state, mousePos, button)
        );
    }

    @Override
    public boolean handleMouseScroll(Vector2D mousePos, int delta) {
        if (!getOpenPanes().isEmpty())
            getOpenPanes().get(getOpenPanes().size() - 1).handleMouseScroll(mousePos, delta);
        return ItrUtil.orMap(
                ItrUtil.getAllOfType(MouseScrollListener.class, getGuiRoot().getChildren(), getHudRoot().getChildren()),
                b -> b.handleMouseScroll(mousePos, delta)
        );
    }
}
