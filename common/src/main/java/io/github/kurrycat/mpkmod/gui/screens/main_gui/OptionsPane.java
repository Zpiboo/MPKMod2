package io.github.kurrycat.mpkmod.gui.screens.main_gui;

import io.github.kurrycat.mpkmod.Main;
import io.github.kurrycat.mpkmod.gui.components.Button;
import io.github.kurrycat.mpkmod.gui.components.Label;
import io.github.kurrycat.mpkmod.gui.components.*;
import io.github.kurrycat.mpkmod.gui.screens.options_gui.Option;
import io.github.kurrycat.mpkmod.util.ItrUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;
import io.github.kurrycat.mpkmod.util.WorldToFile;

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
        ).setPercentFlag(PERCENT.ALL).setAnchors(Anchor.CENTER);
        addChild(optionList);

        OptionItem pkcOption = new OptionItem(optionList);
        pkcOption.setHeight(20);
        TextRectangle radiusText = new TextRectangle(
                new Vector2D(0, 0),
                new Vector2D(45, 1),
                "Radius:",
                new Color(0, 0, 0, 0),
                Color.WHITE
        ).setPercentFlag(PERCENT.SIZE_Y);
        pkcOption.addChild(radiusText);
        Div pkcContent = new Div(new Vector2D(0, 0), new Vector2D(-2, -2))
                .setAnchors(Anchor.CENTER);
        pkcOption.addChild(pkcContent);
        pkcOption.stretchXBetween(pkcContent, radiusText, null);
        NumberSlider pkcFileRadius = new NumberSlider(
                1, 20, 1, 5,
                new Vector2D(0, 0),
                new Vector2D(0.45D, 1),
                v -> {
                }
        ).setPercentFlag(PERCENT.ALL);
        pkcContent.addChild(pkcFileRadius);
        pkcContent.addChild(
                new Button("Save as PKC File",
                        new Vector2D(1 / 2D, 0),
                        new Vector2D(1 / 2D, 1),
                        mouseButton -> WorldToFile.parseWorld((int) pkcFileRadius.getValue())
                ).setPercentFlag(PERCENT.ALL)
        );

        optionList.addItem(pkcOption);

        OptionItem fontSizeOption = new OptionItem(optionList);
        fontSizeOption.setHeight(20);

        TextRectangle fontSizeText = new TextRectangle(
                new Vector2D(0, 0),
                new Vector2D(100, 1),
                "Default Font Size:",
                new Color(0, 0, 0, 0),
                Color.WHITE
        ).setPercentFlag(PERCENT.SIZE_Y);
        fontSizeOption.addChild(fontSizeText);

        Div fontSizeContent = new Div(new Vector2D(0, 0), new Vector2D(-2, -2))
                .setAnchors(Anchor.CENTER);
        fontSizeOption.addChild(fontSizeContent);
        fontSizeOption.stretchXBetween(fontSizeContent, fontSizeText, null);
        fontSizeContent.addChild(
                new NumberSlider(
                        2, 30, 1, Label.DEFAULT_FONT_SIZE,
                        new Vector2D(0, 0),
                        new Vector2D(1, 1),
                        v -> {
                            for(Label l : ItrUtil.getAllOfType(Label.class, Main.mainGUI.getHudRoot().getChildren())) {
                                if(l.fontSize == Label.DEFAULT_FONT_SIZE) l.fontSize = v;
                            }
                            Label.DEFAULT_FONT_SIZE = v;
                        }
                ).setPercentFlag(PERCENT.SIZE).setAnchors(Anchor.TOP_RIGHT)
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
