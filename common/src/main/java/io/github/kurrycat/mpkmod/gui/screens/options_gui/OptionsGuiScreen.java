package io.github.kurrycat.mpkmod.gui.screens.options_gui;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.gui.MPKGuiScreen;
import io.github.kurrycat.mpkmod.gui.components.Anchor;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.Component.PERCENT;
import io.github.kurrycat.mpkmod.gui.components.ScrollableList;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.util.ArrayList;

public class OptionsGuiScreen extends MPKGuiScreen {
    private OptionList optionList;

    @Override
    public boolean shouldCreateKeyBind() {
        return true;
    }

    @Override
    public void onGuiInit() {
        super.onGuiInit();
        optionList = new OptionList(
                new Vector2D(0, 16),
                new Vector2D(3 / 5D, -40),
                new ArrayList<>(API.optionsMap.values())
        );
        getGuiRoot().addChild(optionList
                .setPercentFlag(PERCENT.SIZE_X)
                .setAnchors(Anchor.TOP_CENTER)
        );

        optionList.topCover.addChild(
                new Button(
                        "x",
                        new Vector2D(5, 1),
                        new Vector2D(11, 11),
                        mouseButton -> close()
                ).setAnchors(Anchor.CENTER_RIGHT)
        );

        optionList.bottomCover.setHeight(24, false);
        optionList.bottomCover.backgroundColor = null;

        optionList.bottomCover.addChild(new Button(
                        "Apply",
                        new Vector2D(-2, 2),
                        new Vector2D(100, 20),
                        mouseButton -> optionList.updateAll()
                ).setAnchor(Anchor.BOTTOM_RIGHT).setParentAnchor(Anchor.BOTTOM_CENTER)
        );

        optionList.bottomCover.addChild(new Button(
                        "Reset all",
                        new Vector2D(2, 2),
                        new Vector2D(100, 20),
                        mouseButton -> optionList.resetAllToDefault()
                ).setAnchor(Anchor.BOTTOM_LEFT).setParentAnchor(Anchor.BOTTOM_CENTER)
        );
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Option.saveOptionMapToJSON();
    }


    @Override
    public void renderScreen(Vector2D mouse, float partialTicks) {
        super.renderScreen(mouse, partialTicks);
        optionList.renderHover(mouse);
    }

    public static class OptionList extends ScrollableList<OptionListItem> {
        public OptionList(Vector2D pos, Vector2D size, ArrayList<Option> options) {
            this.setPos(pos);
            this.setSize(size);
            this.setTitle("Options");
            clearItems();
            for (Option option : options) {
                if(!option.shouldShowInOptionList()) continue;

                OptionListItem item;
                switch (option.getType()) {
                    case BOOLEAN:
                        item = new OptionListItemBoolean(this, option);
                        break;
                    case STRING:
                        item = new OptionListItemString(this, option);
                        break;
                    case INTEGER:
                        item = new OptionListItemInteger(this, option);
                        break;
                    default:
                        item = new OptionListItemDefault(this, option);
                }
                addItem(item);
            }
        }

//        @Override
//        public void render(Vector2D mouse) {
//            super.render(mouse);
//            renderComponents(mouse);
//        }

        public void resetAllToDefault() {
            for (OptionListItem item : getItems()) {
                item.loadDefaultValue();
            }
        }

        public void updateAll() {
            for (OptionListItem item : getItems()) {
                item.updateValue();
            }
        }
    }
}
