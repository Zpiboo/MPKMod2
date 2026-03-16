package io.github.kurrycat.mpkmod.ticks;

public class VariableMatch {
    public final VariableType type;
    public final String name;

    public VariableMatch(VariableType type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getRegex() {
        String typeRegex = type.needsTypePrefix() ? type + ":" : "";
        return "\\{" + typeRegex + name + "}";
    }
}
