package io.github.kurrycat.mpkmod.gui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.KeyBinding;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.gui.screens.main_gui.MainGuiScreen;
import io.github.kurrycat.mpkmod.util.Mouse;
import io.github.kurrycat.mpkmod.util.StringUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class KeyBindingLabel extends ResizableComponent {
    @JsonProperty
    public Color keyDownColor = new Color(255, 255, 255, 95);
    @JsonProperty
    public Color keyUpColor = new Color(31, 31, 31, 47);
    @JsonProperty
    public Color selectedColor = new Color(255, 170, 0, 100);
    @JsonProperty("name")
    private String name;
    private KeyBinding keyBinding;
    @JsonProperty("displayName")
    private String displayName;
    private boolean keyDown;
    private Color renderColor;

    public KeyBindingLabel(Vector2D pos, Vector2D size, String name) {
        this.setPos(pos);
        this.setSize(size);
        this.name = name;
        updateKeyBinding();
    }

    private void updateKeyBinding() {
        this.keyBinding = KeyBinding.getByName(name);
        this.displayName = keyBinding == null ? name : StringUtil.capitalize(keyBinding.getDisplayName());
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public KeyBindingLabel(@JsonProperty("name") String name, @JsonProperty("displayName") String displayName) {
        this.setPos(pos);
        this.setSize(size);
        this.name = name;
        this.keyBinding = KeyBinding.getByName(name);
        updateKeyBinding();
        if (displayName != null)
            this.displayName = displayName;
    }

    @Override
    protected void update() {
        keyDown = keyBinding != null && keyBinding.isKeyDown();
        renderColor = selected ? selectedColor : (keyDown ? keyDownColor : keyUpColor);
    }

    @Override
    public void render(Vector2D mouse) {
        if (highlighted) Renderer2D.drawDottedRect(getDisplayedPos(), getDisplayedSize(), 1, 1, 1, Color.BLACK);
        Renderer2D.drawRect(getDisplayedPos(), getDisplayedSize(), renderColor);

        FontRenderer.drawCenteredString(
                displayName,
                getDisplayedPos().add(getDisplayedSize().div(2)).add(new Vector2D(0, 1)),
                keyDown ? Color.BLACK : Color.WHITE,
                false
        );
        renderHoverEdges(mouse);
    }

    @Override
    public PopupMenu getPopupMenu() {
        PopupMenu menu = new PopupMenu();

        KeyBindingLabel.EditPane editPane = new KeyBindingLabel.EditPane(
                Vector2D.ZERO,
                new Vector2D(0.4, 0.5)
        );

        menu.addComponent(new Button("Edit", mouseButton -> {
            if (Mouse.Button.LEFT.equals(mouseButton)) {
                menu.screen.getGuiRoot().passPositionTo(editPane
                        .setPercentFlag(PERCENT.SIZE)
                        .setAnchors(Anchor.CENTER)
                );
                menu.screen.openPane(editPane);
                menu.close();
            }
        }));
        menu.addComponent(new Button("Delete", mouseButton -> {
            if (Mouse.Button.LEFT.equals(mouseButton)) {
                menu.screen.removeHudComponent(this);
                menu.close();
            }
        }));
        return menu;
    }

    @JsonGetter("name")
    public String getName() {
        return this.name;
    }

    private class EditPane extends Pane<MainGuiScreen> {
        private final TextRectangle downKey;
        private final TextRectangle upKey;

        public EditPane(Vector2D pos, Vector2D size) {
            super(pos, size);
            this.upKey = new TextRectangle(
                    new Vector2D(-1 / 6D, 5),
                    KeyBindingLabel.this.getDisplayedSize(),
                    displayName,
                    keyUpColor,
                    Color.WHITE);
            this.addChild(upKey
                    .setPercentFlag(PERCENT.POS_X)
                    .setAnchors(Anchor.TOP_CENTER)
            );
            this.downKey = new TextRectangle(
                    new Vector2D(1 / 6D, 5),
                    KeyBindingLabel.this.getDisplayedSize(),
                    displayName,
                    keyDownColor,
                    Color.BLACK);
            this.addChild(downKey
                    .setPercentFlag(PERCENT.POS_X)
                    .setAnchors(Anchor.TOP_CENTER)
            );

            this.addChild(
                    new ColorSelector(keyUpColor, new Vector2D(0.2, 0.4), color -> keyUpColor = color)
                            .setPercentFlag(PERCENT.POS)
                            .setAnchors(Anchor.TOP_LEFT)
            );
            this.addChild(
                    new ColorSelector(keyDownColor, new Vector2D(0.2, 0.4), color -> keyDownColor = color)
                            .setPercentFlag(PERCENT.POS)
                            .setAnchors(Anchor.TOP_RIGHT)
            );


            this.addChild(
                    new InputField(name, new Vector2D(0, InputField.HEIGHT + 7), 0.95D)
                            .setOnContentChange(content -> {
                                name = content.getContent();
                                keyBinding = KeyBinding.getByName(name);
                            })
                            .setPercentFlag(PERCENT.X)
                            .setAnchors(Anchor.BOTTOM_CENTER)
            );
            this.addChild(
                    new InputField(displayName, new Vector2D(0, 5), 0.95D)
                            .setOnContentChange(content -> {
                                displayName = content.getContent();
                                this.downKey.setText(displayName);
                                this.upKey.setText(displayName);
                            })
                            .setPercentFlag(PERCENT.X)
                            .setAnchors(Anchor.BOTTOM_CENTER)
            );

            KeySetList list = new KeySetList(new Vector2D(0.025, 10), new Vector2D(0.25, -20));
            list.setAbsolute(true);
            this.addChild(list.setPercentFlag(Component.PERCENT.POS_X | Component.PERCENT.SIZE_X));

            SortedSet<String> keyMap = new TreeSet<>(KeyBinding.getKeyMap().keySet());
            HashMap<String, ArrayList<String>> modMap = new HashMap<>();
            for (String s : keyMap) {
                String modName = s.substring(0, !s.contains(".") ? s.length() : s.indexOf("."));
                if (modName.equals("key")) modName = "Minecraft";

                if (!modMap.containsKey(modName)) modMap.put(modName, new ArrayList<>());
                modMap.get(modName).add(s);
            }
            list.addItem(new KeySetListItem(list, "Minecraft (key.)", modMap.get("Minecraft")));
            modMap.remove("Minecraft");

            for (String modName : new TreeSet<>(modMap.keySet())) {
                String keyPrefix = "No Prefix found";

                String exampleKey = modMap.get(modName).isEmpty() ? null : modMap.get(modName).get(0);
                if (exampleKey != null) {
                    if (exampleKey.contains(".key.")) {
                        keyPrefix = exampleKey.substring(0, exampleKey.indexOf(".key.") + 5);
                    } else if (exampleKey.contains(".")) {
                        keyPrefix = exampleKey.substring(0, exampleKey.indexOf(".") + 1);
                    }
                }

                list.addItem(new KeySetListItem(
                        list,
                        modName + " (" + keyPrefix + ")",
                        modMap.get(modName)
                ));
            }
        }

        @Override
        public void render(Vector2D mousePos) {
            upKey.setColor(keyUpColor);
            downKey.setColor(keyDownColor);
            super.render(mousePos);
        }

        private class KeySetList extends ScrollableList<KeySetListItem> {
            public KeySetList(Vector2D pos, Vector2D size) {
                this.setPos(pos);
                this.setSize(size);
                this.setTitle("Keybinding Names");
            }
        }

        private class KeySetListItem extends ScrollableListItem<KeySetListItem> {
            private static final int keyItemHeight = 12;
            private final String modName;
            private final ArrayList<String> keyNames;
            private final Button collapseButton;
            private boolean collapsed = true;

            public KeySetListItem(ScrollableList<KeySetListItem> parent, String modName, ArrayList<String> keyNames) {
                super(parent);
                this.modName = modName;
                this.keyNames = keyNames;
                this.setHeight(13);
                collapseButton = new Button("v", new Vector2D(1,1), new Vector2D(11, 11));
                collapseButton.setButtonCallback(mouseButton -> {
                    if (mouseButton != Mouse.Button.LEFT) return;
                    collapsed = !collapsed;
                    collapseButton.setText(collapsed ? "v" : "^");
                    collapseButton.textOffset = collapsed ? Vector2D.ZERO : new Vector2D(0, 3);
                });
                addChild(collapseButton.setAnchors(Anchor.TOP_RIGHT));
            }

            @Override
            public int getHeight() {
                return collapsed ? 13 : keyItemHeight * (keyNames.size() + 1) + 1;
            }

            @Override
            public void render(Vector2D mouse) {
                if (collapsed) {
                    Renderer2D.drawRectWithEdge(getDisplayedPos(), getDisplayedSize(), 1, new Color(31, 31, 31, 150), new Color(31, 31, 31, 150));
                    FontRenderer.drawCenteredString(modName, getDisplayedPos().add(getDisplayedSize().div(2)), Color.WHITE, false);
                } else {
                    Renderer2D.drawRectWithEdge(getDisplayedPos(),
                            new Vector2D(getDisplayedSize().getX(), keyItemHeight),
                            1, new Color(31, 31, 31, 150),
                            new Color(31, 31, 31, 150));
                    Renderer2D.drawRect(
                            getDisplayedPos().add(0, keyItemHeight),
                            getDisplayedSize().sub(0, keyItemHeight),
                            new Color(31, 31, 31, 150));
                    FontRenderer.drawCenteredString(modName, getDisplayedPos().add(getDisplayedSize().getX() / 2, keyItemHeight / 2D), Color.WHITE, false);

                    for (int i = 0; i < keyNames.size(); i++) {
                        FontRenderer.drawCenteredString(
                                keyNames.get(i),
                                getDisplayedPos().add(getDisplayedSize().getX() / 2, keyItemHeight * 1.5D + 12 * i),
                                Color.WHITE, false
                        );
                    }
                }

                super.render(mouse);
            }
        }
    }
}
