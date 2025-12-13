package io.github.kurrycat.mpkmod.compatibility.fabric_1_21_9.mixin;

import io.github.kurrycat.mpkmod.compatibility.fabric_1_21_9.MPKMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderBlockEntities", at = @At("HEAD"))
    public void render(MatrixStack matrices, WorldRenderState renderState, OrderedRenderCommandQueueImpl orderedRenderCommandQueueImpl, CallbackInfo ci) {
        MPKMod.INSTANCE.eventHandler.onRenderWorldOverlay(
                new MatrixStack(),
                MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true)
        );
    }
}
