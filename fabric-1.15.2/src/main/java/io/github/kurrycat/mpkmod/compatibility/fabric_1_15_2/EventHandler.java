package io.github.kurrycat.mpkmod.compatibility.fabric_1_15_2;

import io.github.kurrycat.mpkmod.compatibility.API;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import io.github.kurrycat.mpkmod.compatibility.MCClasses.Player;
import io.github.kurrycat.mpkmod.compatibility.fabric_1_15_2.mixin.KeyBindingAccessor;
import io.github.kurrycat.mpkmod.ticks.ButtonMS;
import io.github.kurrycat.mpkmod.ticks.ButtonMSList;
import io.github.kurrycat.mpkmod.util.BoundingBox3D;
import io.github.kurrycat.mpkmod.util.Vector3D;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class EventHandler {
    private static final ButtonMSList timeQueue = new ButtonMSList();

    /**
     * @param key      The GLFW key code. See {@link net.minecraft.client.util.InputUtil}.
     * @param scanCode
     * @param action   The action, where 0 = unpressed, 1 = pressed, 2 = held.
     */
    public void onKey(int key, int scanCode, int action) {
        GameOptions options = MinecraftClient.getInstance().options;
        long eventNanos = Util.getMeasuringTimeNano();

        InputUtil.KeyCode inputKey = InputUtil.getKeyCode(key, scanCode);

        int[] keys = {
                ((KeyBindingAccessor) options.keyForward).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keyLeft).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keyBack).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keyRight).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keySprint).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keySneak).getKeyCode().getKeyCode(),
                ((KeyBindingAccessor) options.keyJump).getKeyCode().getKeyCode()
        };

        for (int i = 0; i < keys.length; i++) {
            if (key == keys[i]) {
                timeQueue.add(ButtonMS.of(ButtonMS.Button.values()[i], eventNanos, action == 1));
            }
        }

        if (action == 1) {
            FunctionCompatibility.pressedButtons.add(inputKey.getKeyCode());
        } else if (action == 0) {
            FunctionCompatibility.pressedButtons.remove(inputKey.getKeyCode());
        }

        String name = InputUtil.getKeycodeName(key);
        if (name == null) {
            name = InputUtil.getScancodeName(scanCode);
        }
        if (name == null) {
            name = InputUtil.getKeyCode(key, scanCode).getName();
        }
        API.Events.onKeyInput(key, /*dummyKeyBinding.getLocalizedName()*/name, action == 1);

        if (action != 0)
            checkKeyBinding(key);
    }

    public void onMouseMove(double x, double y, double dx, double dy) {
        API.Events.onMouseInput(
                io.github.kurrycat.mpkmod.util.Mouse.Button.NONE,
                io.github.kurrycat.mpkmod.util.Mouse.State.NONE,
                (int) x, (int) y, (int) dx, (int) dy,
                0, System.nanoTime()
        );
    }

    public void onMouseScroll(double vertical, double x, double y) {
        API.Events.onMouseInput(
                io.github.kurrycat.mpkmod.util.Mouse.Button.NONE,
                io.github.kurrycat.mpkmod.util.Mouse.State.NONE,
                (int) x, (int) y, 0, 0,
                (int) vertical, System.nanoTime()
        );
    }

    public void onMouseButton(int button, int action, double x, double y) {
        API.Events.onMouseInput(
                io.github.kurrycat.mpkmod.util.Mouse.Button.fromInt(button),
                button == -1 ? io.github.kurrycat.mpkmod.util.Mouse.State.NONE :
                        (action == 1 ? io.github.kurrycat.mpkmod.util.Mouse.State.DOWN : io.github.kurrycat.mpkmod.util.Mouse.State.UP),
                (int) x, (int) y, 0, 0,
                0, System.nanoTime()
        );

        if (action == 1)
            checkKeyBinding(button);
    }

    private void checkKeyBinding(int keyCode) {
        if (MinecraftClient.getInstance().currentScreen != null) return;

        for (Map.Entry<String, KeyBinding> keyBindingEntry : MPKMod.keyBindingMap.entrySet()) {
            InputUtil.KeyCode boundKey = ((KeyBindingAccessor) keyBindingEntry.getValue()).getKeyCode();
            String keyBindId = keyBindingEntry.getKey();

            if (boundKey.getKeyCode() == keyCode) {
                API.Events.onKeybind(keyBindId);
                return;
            }
        }
    }

    public void onInGameOverlayRender(float tickDelta) {
        API.Events.onRenderOverlay();
    }

    public void onRenderWorldOverlay(MatrixStack matrixStack, float tickDelta) {
        matrixStack.push();
        Vec3d pos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        matrixStack.translate(-pos.x, -pos.y, -pos.z);
        API.Events.onRenderWorldOverlay(tickDelta);
        matrixStack.pop();
    }

    public void onClientTickStart(MinecraftClient mc) {
        if (mc.isPaused() || mc.world == null) return;
        API.Events.onTickStart();
    }

    public void onClientTickEnd(MinecraftClient mc) {
        if (mc.isPaused() || mc.world == null) return;
        ClientPlayerEntity mcPlayer = mc.player;

        if (mcPlayer != null) {
            Box playerBB = mcPlayer.getBoundingBox();
            new Player()
                    .setPos(new Vector3D(mcPlayer.getX(), mcPlayer.getY(), mcPlayer.getZ()))
                    .setLastPos(new Vector3D(mcPlayer.prevX, mcPlayer.prevY, mcPlayer.prevZ))
                    .setMotion(new Vector3D(mcPlayer.getVelocity().x, mcPlayer.getVelocity().y, mcPlayer.getVelocity().z))
                    .setRotation(mcPlayer.getRotationClient().y, mcPlayer.getRotationClient().x)
                    .setOnGround(mcPlayer.onGround)
                    .setSprinting(mcPlayer.isSprinting())
                    .setBoundingBox(new BoundingBox3D(
                            new Vector3D(playerBB.x1, playerBB.y1, playerBB.z1),
                            new Vector3D(playerBB.x2, playerBB.y2, playerBB.z2)
                    ))
                    .setFlying(mcPlayer.abilities.flying)
                    .constructKeyInput()
                    .setKeyMSList(timeQueue)
                    .buildAndSave();
            timeQueue.clear();
        }

        //TODO: Dirty fix for getting the player ping in the right thread
        ClientPlayNetworkHandler nh = MinecraftClient.getInstance().getNetworkHandler();
        if (nh != null) {
            nh.getPlayerList().stream()
                    .filter(playerInfo -> mcPlayer != null && playerInfo.getProfile().getId().equals(mcPlayer.getUuid()))
                    .findFirst().ifPresent(playerInfo -> Minecraft.ping = playerInfo.getLatency());
        }

        API.Events.onTickEnd();
    }


    public void onServerConnect(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        API.Events.onServerConnect(clientPlayNetworkHandler.getConnection().isLocal());
    }

    public void onServerDisconnect(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        API.Events.onServerDisconnect();
    }
}
