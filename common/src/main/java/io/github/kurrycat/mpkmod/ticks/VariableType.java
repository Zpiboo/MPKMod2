package io.github.kurrycat.mpkmod.ticks;

public enum VariableType {
    TICKS("");

    private final String id;

    VariableType(String id) {
        this.id = id;
    }

    public boolean needsTypePrefix() {
        return this != TICKS;
    }

    @Override
    public String toString() {
        return id;
    }

    public static VariableType fromString(String id) {
        for (VariableType vt : values()) {
            if (vt.id.equals(id))
                return vt;
        }
        return null;
    }
}
