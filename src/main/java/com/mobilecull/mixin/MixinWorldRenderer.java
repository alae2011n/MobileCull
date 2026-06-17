package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class, priority = 999)
public class MixinWorldRenderer {

    @Inject(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Frustum;setPosition(DDD)V")
    )
    private void captureFrustumMatrices(
        RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, 
        GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, 
        Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci
    ) {
        Matrix4f combinedMvp = new Matrix4f(projectionMatrix).mul(positionMatrix);
        MobileCullEngine.INSTANCE.updateFrustum(combinedMvp);
    }
}
