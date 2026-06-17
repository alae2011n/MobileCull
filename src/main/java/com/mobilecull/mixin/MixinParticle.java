package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public class MixinParticle {

    @Inject(
        method = "buildGeometry",
        at = @At("HEAD"),
        cancellable = true
    )
    public void onBuildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci) {
        if (!MobileCullEngine.INSTANCE.shouldRender((Particle) (Object) this)) {
            ci.cancel();
        }
    }
}
