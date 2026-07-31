package io.github.kurrycat.mpkmod.compatibility.api;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.events.Event;
import io.github.kurrycat.mpkmod.gui.MPKGuiScreen;
import io.github.kurrycat.mpkmod.gui.infovars.InfoString;
import io.github.kurrycat.mpkmod.util.input.TickInput;
import io.github.kurrycat.mpknetapi.common.network.packet.MPKPacket;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public abstract class ClientApi {

    protected abstract String _getIp();
    protected abstract String _getFPS();
    protected abstract int _getPing();
    protected abstract void _displayGuiScreen(MPKGuiScreen screen);
    protected abstract String _getCurrentGuiScreen();
    protected abstract String _getUserName();
    protected abstract void _copyToClipboard(String content);
    protected abstract boolean _setInputs(Float yaw, boolean relYaw, Float pitch, boolean relPitch, int pressedInputs, int releasedInputs, int L, int R);
    protected abstract boolean _isF3Enabled();
    protected abstract void _sendPacket(MPKPacket packet);

    public static String version;
    public static String vfpVersion = null;
    public static WorldState worldState = WorldState.MENU;
    public static PlayState playState = PlayState.ACTIVE;
    public static boolean sprintToggled = false;
    public static int ping = -1;

    @InfoString.Getter
    public static String getIp() {
        if (isSingleplayer()) return "Singleplayer";
        String ip = _getIp();
        return _getIp()-> {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to get IP, are you playing on an unsupported minecraft version?");
            return "Failed getting IP";
        });
    }

    @InfoString.Getter
    public static boolean isSingleplayer() {
        return worldState == Minecraft.WorldState.SINGLE_PLAYER;
    }

    @InfoString.Getter
    public static String getFps() {
        return Minecraft.Interface.get().map(Minecraft.Interface::getFPS).orElseGet(() -> {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to get FPS, are you playing on an unsupported minecraft version?");
            return "Error";
        });
    }

    @InfoString.Getter
    public static int getPing() {
        return Minecraft.Interface.get().map(Minecraft.Interface::getPing).orElseGet(() -> {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to get Ping, are you playing on an unsupported minecraft version?");
            return -1;
        });
    }

    @InfoString.Getter
    public static String getMcVersion() {
        return vfpVersion == null ? version : vfpVersion + " (VFP)";
    }

    @InfoString.Getter
    public static String getUsername() {
        if (!Minecraft.Interface.get().isPresent()) {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to get username, are you playing on an unsupported minecraft version?");
            return "Error";
        } else {
            return Minecraft.Interface.get().get().getUserName();
        }
    }

    public static String getCurrentGuiScreen() {
        if (!Minecraft.Interface.get().isPresent()) {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to get current screen name, are you playing on an unsupported minecraft version?");
            return "Error";
        } else {
            return Minecraft.Interface.get().get().getCurrentGuiScreen();
        }
    }

    @InfoString.Getter
    public static String getTime() {
        return new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    @InfoString.Getter
    public static String getDate() {
        return new SimpleDateFormat("dd/MM/yy").format(Calendar.getInstance().getTime());
    }

    public static void updateWorldState(Event.EventType type, boolean isLocal) {
        if (type == Event.EventType.SERVER_CONNECT) {
            if (isLocal) worldState = Minecraft.WorldState.SINGLE_PLAYER;
            else worldState = Minecraft.WorldState.MULTI_PLAYER;
        } else worldState = Minecraft.WorldState.MENU;
    }

    public static void displayGuiScreen(MPKGuiScreen screen) {
        Minecraft.Interface.get().ifPresent(i -> i.displayGuiScreen(screen));
    }

    public static void copyToClipboard(String str) {
        Minecraft.Interface.get().ifPresent(i -> i.copyToClipboard(str));
    }

    public static boolean setInputs(TickInput inputs) {
        return setInputs(inputs.getYaw(), true,
                inputs.getPitch(), true,
                inputs.getKeyInputs(), ~inputs.getKeyInputs(),
                inputs.getL(), inputs.getR());
    }

    public static boolean setInputs(Float yaw, boolean relYaw, Float pitch, boolean relPitch, int pressedInputs, int releasedInputs, int L, int R) {
        if (!Minecraft.isSingleplayer()) return false;
        if (!Minecraft.Interface.get().isPresent()) {
            API.LOGGER.info(API.COMPATIBILITY_MARKER, "Failed to set inputs, are you playing on an unsupported minecraft version?");
            return false;
        }
        return Minecraft.Interface.get().get().setInputs(yaw, relYaw, pitch, relPitch, pressedInputs, releasedInputs, L, R);
    }

    public static boolean isF3Enabled() {
        return Minecraft.Interface.get().map(Minecraft.Interface::isF3Enabled).orElse(false);
    }

    @InfoString.Getter
    public static boolean isSprintToggled() {
        return sprintToggled;
    }

    public static void toggleSprint() {
        sprintToggled = !sprintToggled;
    }


    public enum WorldState {
        MENU,
        SINGLE_PLAYER,
        MULTI_PLAYER;
    }

    public enum PlayState {
        ACTIVE,
        AFK;
    }
}
