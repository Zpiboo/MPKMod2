package io.github.kurrycat.mpkmod.compatibility.MCClasses;

import java.util.HashMap;
import java.util.function.Supplier;

public class KeyBinding {
    private static final HashMap<String, KeyBinding> keyMap = new HashMap<>();
    private final String name;
    private final Supplier<Boolean> isKeyDownSupplier;
    private final Supplier<String> displayName;

    private boolean tickState = false;

    public KeyBinding(Supplier<String> displayName, String name, Supplier<Boolean> isKeyDownSupplier) {
        this.displayName = displayName;
        this.name = name;
        this.isKeyDownSupplier = isKeyDownSupplier;

        if (!keyMap.containsKey(this.name))
            keyMap.put(this.name, this);
    }

    public static KeyBinding getByName(String name) {
        return keyMap.get(name);
    }

    public static HashMap<String, KeyBinding> getKeyMap() {
        return keyMap;
    }

    public static void updateKeyStates() {
        for (KeyBinding k : getKeyMap().values())
            k.updateTickState();
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public String getName() {
        return name;
    }

    private void updateTickState() {
        tickState = isKeyDownSupplier.get();
    }

    public boolean getTickState() {
        return tickState;
    }
}
