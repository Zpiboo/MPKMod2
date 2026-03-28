package io.github.kurrycat.mpkmod.gui.screens.options_gui;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.gui.components.ScrollableList;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;

public class OptionListItemDefault extends OptionListItem {
    public OptionListItemDefault(ScrollableList<OptionListItem> parent, Option option) {
        super(parent, option);
    }

    @Override
    protected void updateDisplayValue() {
    }

    @Override
    protected void renderTypeSpecific(Vector2D mouse) {
        FontRenderer.drawRightCenteredString(
                value,
                getDisplayedPos().add(5, getDisplayedSize().getY() / 2),
                Color.WHITE,
                false
        );
    }
}
