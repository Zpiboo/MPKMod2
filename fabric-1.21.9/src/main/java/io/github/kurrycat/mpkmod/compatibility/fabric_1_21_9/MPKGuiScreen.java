package io.github.kurrycat.mpkmod.compatibility.fabric_1_21_9;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Profiler;
import io.github.kurrycat.mpkmod.util.MathUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class MPKGuiScreen extends Screen {
    public io.github.kurrycat.mpkmod.gui.MPKGuiScreen eventReceiver;

    public MPKGuiScreen(io.github.kurrycat.mpkmod.gui.MPKGuiScreen screen) {
        super(Text.translatable(API.MODID + ".gui.title"));
        eventReceiver = screen;
    }

    @Override
    public void init() {
        eventReceiver.onInit();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        eventReceiver.onResize(width, height);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        drawContext.getMatrices().pushMatrix();
        API.<FunctionCompatibility>getFunctionHolder().drawContext = drawContext;
        Profiler.startSection(eventReceiver.getID() == null ? "mpk_gui" : eventReceiver.getID());
        try {
            eventReceiver.drawScreen(new Vector2D(mouseX, mouseY), delta);
        } catch (Exception e) {
            API.LOGGER.warn("Error in drawScreen with id: " + eventReceiver.getID(), e);
        }
        Profiler.endSection();
        drawContext.getMatrices().popMatrix();
    }

    @Override
    public void close() {
        super.close();
        eventReceiver.onGuiClosed();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        eventReceiver.onMouseClicked(new Vector2D(click.x(), click.y()), click.button());
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        eventReceiver.onMouseReleased(new Vector2D(click.x(), click.y()), click.button());
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        eventReceiver.onMouseClickMove(new Vector2D(click.x(), click.y()), click.button(), 0);
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        eventReceiver.onKeyEvent(input.key(), input.scancode(), input.modifiers(), false);
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        eventReceiver.onKeyEvent(input.codepoint(), 0, input.modifiers(), true);
        return super.charTyped(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        eventReceiver.onMouseScroll(
                new Vector2D(mouseX, mouseY),
                (int) (MathUtil.constrain(verticalAmount, -1, 1) * 7)
        );
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
