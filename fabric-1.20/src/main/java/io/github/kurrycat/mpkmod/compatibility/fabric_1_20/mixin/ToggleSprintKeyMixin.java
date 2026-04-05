package io.github.kurrycat.mpkmod.compatibility.fabric_1_20.mixin;

import io.github.kurrycat.mpkmod.compatibility.MCClasses.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class ToggleSprintKeyMixin {
    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void sprintOverride(CallbackInfoReturnable<Boolean> cir) {
        KeyBinding self = (KeyBinding) (Object) this;
        KeyBinding sprintKey = MinecraftClient.getInstance().options.sprintKey;

        if (self == sprintKey && Minecraft.isSprintToggled())
            cir.setReturnValue(true);
    }
}