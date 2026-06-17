package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public abstract class MixinParticle {
    @Shadow protected double x;
    @Shadow protected double y;
    @Shadow protected double z;

    @Inject(method = "buildGeometry", at = @At("HEAD"), cancellable = true)
    private void cullParticleRender(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci) {
        MobileCullEngine engine = MobileCullEngine.INSTANCE;
        if (engine.cullParticles) {
            // Check visibility using a tightly bound bounding point for lightning-fast lookups
            boolean visible = engine.isBoxVisible(
                this.x - 0.1, this.y - 0.1, this.z - 0.1,
                this.x + 0.1, this.y + 0.1, this.z + 0.1
            );
            if (!visible) {
                ci.cancel();
            }
        }
    }
}

