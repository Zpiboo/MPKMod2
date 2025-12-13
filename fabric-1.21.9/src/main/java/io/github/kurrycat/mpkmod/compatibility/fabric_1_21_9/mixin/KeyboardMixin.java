package io.github.kurrycat.mpkmod.compatibility.fabric_1_21_9.mixin;

import io.github.kurrycat.mpkmod.compatibility.fabric_1_21_9.MPKMod;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At(value = "RETURN"))
    private void onKey(long window, int action, KeyInput keyInput, CallbackInfo ci) {
        if (keyInput.key() != -1) {
            MPKMod.INSTANCE.eventHandler.onKey(keyInput, action);
        }
    }
}
