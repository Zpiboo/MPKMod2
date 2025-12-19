package io.github.kurrycat.mpkmod.gui.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.FontRenderer;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Renderer2D;
import io.github.kurrycat.mpkmod.util.MathUtil;
import io.github.kurrycat.mpkmod.util.Vector2D;

import java.awt.*;

public class TurnsLabel extends Component {
    private static final double ROW_HEIGHT = 11;

    public double fontSize = FontRenderer.DEFAULT_FONT_SIZE;

    private int firstTick;
    private int lastTick;
    private int decimals;
    private boolean keepZeros;

    public TurnsLabel() {
        this(3, 11, 5, true);
    }

    @JsonCreator
    public TurnsLabel(
            @JsonProperty("firstTick") int firstTick,
            @JsonProperty("lastTick") int lastTick,
            @JsonProperty("decimals") int decimals,
            @JsonProperty("keepZeros") boolean keepZeros
    ) {
        this.firstTick = firstTick;
        this.lastTick = lastTick;
        this.decimals = decimals;
        this.keepZeros = keepZeros;
        validateTicks();
    }

    private void validateTicks() {
        if (firstTick < 1) {
            firstTick = 1;
        } else if (firstTick >= Player.MAX_TURNS) {
            firstTick = Player.MAX_TURNS - 1;
        }

        if (lastTick < firstTick) {
            lastTick = firstTick;
        } else if (lastTick >= Player.MAX_TURNS) {
            lastTick = Player.MAX_TURNS - 1;
        }
    }

    @JsonGetter("firstTick")
    public int getFirstTick() {
        return firstTick;
    }
    public void setFirstTick(int firstTick) {
        this.firstTick = firstTick;
        validateTicks();
    }

    @JsonGetter("lastTick")
    public int getLastTick() {
        return lastTick;
    }
    public void setLastTick(int lastTick) {
        this.lastTick = lastTick;
        validateTicks();
    }

    @JsonGetter("decimals")
    public int getDecimals() {
        return decimals;
    }
    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    @JsonGetter("keepZeros")
    public boolean getKeepZeros() {
        return keepZeros;
    }
    public void setKeepZeros(boolean keepZeros) {
        this.keepZeros = keepZeros;
    }

    public int getTurnCount() {
        return lastTick - firstTick + 1;
    }

    public float getTurnAt(int i) {
        Player player = Player.getLatest();
        if (player == null) return 0.0F;
        if (i < 0 || Player.MAX_TURNS <= i) return 0.0F;
        return player.turns[i];
    }

    @Override
    public void render(Vector2D mouse) {
        double width = 0.0D;
        for (int i = 0; i < getTurnCount(); i++) {
            String prefix = i+firstTick + ": ";
            double prefixWidth = FontRenderer.getStringSize(prefix, fontSize).getX();
            FontRenderer.drawString(
                    prefix,
                    getDisplayedPos().add(0, i*ROW_HEIGHT),
                    Color.CYAN,
                    fontSize,
                    true
            );
            String value = MathUtil.formatDecimals(getTurnAt(i+firstTick-1), decimals, keepZeros);
            double valueWidth = FontRenderer.getStringSize(value, fontSize).getX();
            FontRenderer.drawString(
                    value,
                    getDisplayedPos().add(prefixWidth, i*ROW_HEIGHT),
                    Color.WHITE,
                    fontSize,
                    true
            );

            width = Math.max(width, prefixWidth + valueWidth);
        }
        setSize(new Vector2D(width, getTurnCount()*ROW_HEIGHT));

        if (selected)
            Renderer2D.drawDottedRect(getDisplayedPos(), getDisplayedSize(), 1, 1, 1, Color.BLACK);
    }
}
