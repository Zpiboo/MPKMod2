package io.github.kurrycat.mpkmod.gui.screens.main_gui;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.Label;
import io.github.kurrycat.mpkmod.gui.components.*;
import io.github.kurrycat.mpkmod.gui.screens.options_gui.Option;
import io.github.kurrycat.mpkmod.util.ItrUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;

public class OptionsPane extends Pane<MainGuiScreen> {
    public OptionsPane(Vector2D pos, Vector2D size) {
        super(pos, size);
        this.backgroundColor = new Color(16, 16, 16, 70);
        addTitle("Options");
        initComponents();
    }

    @Override
    public void close() {
        Option.updateOptionMapFromFields();
        Option.saveOptionMapToJSON();
        super.close();
    }

    private void initComponents() {
        OptionList optionList = new OptionList(
                new Vector2D(0, 0.05),
                new Vector2D(0.9, 0.8)
        );
        addChild(optionList, PERCENT.ALL, Anchor.CENTER);

        OptionItem fontSizeOption = new OptionItem(optionList);
        fontSizeOption.setHeight(20);

        TextRectangle fontSizeText = new TextRectangle(
                new Vector2D(0, 0),
                new Vector2D(100, 1),
                "Default Font Size:",
                new Color(0, 0, 0, 0),
                Color.WHITE
        );
        fontSizeOption.addChild(fontSizeText, PERCENT.SIZE_Y);

        Div fontSizeContent = new Div(new Vector2D(0, 0), new Vector2D(-2, -2));
        fontSizeOption.addChild(fontSizeContent, PERCENT.NONE, Anchor.CENTER);
        fontSizeOption.stretchXBetween(fontSizeContent, fontSizeText, null);
        fontSizeContent.addChild(
                new NumberSlider(
                        2, 30, 1, Label.DEFAULT_FONT_SIZE,
                        new Vector2D(0, 0),
                        new Vector2D(1, 1),
                        v -> {
                            for(Label l : ItrUtil.getAllOfType(Label.class, Main.mainGUI.movableComponents)) {
                                if(l.fontSize == Label.DEFAULT_FONT_SIZE) l.fontSize = v;
                            }
                            Label.DEFAULT_FONT_SIZE = v;
                        }
                ), PERCENT.SIZE, Anchor.TOP_RIGHT
        );
        optionList.addItem(fontSizeOption);

    }

    @Override
    public void render(Vector2D mousePos) {
        super.render(mousePos);
    }

    private static class OptionList extends ScrollableList<OptionItem> {
        public OptionList(Vector2D pos, Vector2D size) {
            super();
            setPos(pos);
            setSize(size);
        }
    }

    private static class OptionItem extends ScrollableListItem<OptionItem> {
        public OptionItem(ScrollableList<OptionItem> parent) {
            super(parent);

        }

        @Override
        public void render(int index, Vector2D pos, Vector2D size, Vector2D mouse) {
            renderDefaultBorder(pos, size);
            renderComponents(mouse);
        }
    }
}
