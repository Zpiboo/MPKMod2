package io.github.kurrycat.mpkmod.compatibility.fabric_1_20_4.mixin;

import io.github.kurrycat.mpkmod.compatibility.fabric_1_20_4.MPKMod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private double cursorDeltaX;
    @Shadow
    private double cursorDeltaY;
    @Shadow
    private double x;
    @Shadow
    private double y;

    @Inject(method = "onCursorPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;updateMouse()V"))
    private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
        MPKMod.INSTANCE.eventHandler.onMouseMove(x, y, cursorDeltaX, -cursorDeltaY);
    }

    @Inject(method = "onMouseScroll", at = @At(value = "TAIL"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MPKMod.INSTANCE.eventHandler.onMouseScroll(vertical, x, y);
    }

    @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        MPKMod.INSTANCE.eventHandler.onMouseButton(button, action, x, y);
    }
}
