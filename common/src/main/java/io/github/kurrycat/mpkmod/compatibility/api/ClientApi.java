package io.github.kurrycat.mpkmod.compatibility.api;

import io.github.kurrycat.mpkmod.gui.MPKGuiScreen;
import io.github.kurrycat.mpknetapi.common.network.packet.MPKPacket;

public interface ClientApi {
    String getIp();
    String getFps();
    int getPing();
    void displayGuiScreen(MPKGuiScreen screen);
    String getCurrentGuiScreen();
    String getUsername();
    void copyToClipboard(String content);
    boolean setInputs(Float yaw, boolean relYaw, Float pitch, boolean relPitch, int pressedInputs, int releasedInputs, int L, int R);
    boolean isF3Enabled();
    void sendPacket(MPKPacket packet);
}
