package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void cullEntityRender(
        E entity, double x, double y, double z, float yaw, float tickDelta,
        MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci
    ) {
        MobileCullEngine engine = MobileCullEngine.INSTANCE;
        if (engine.cullEntities) {
            Box box = entity.getBoundingBox();
            
            boolean visible = engine.isBoxVisible(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
            );

            if (!visible) {
                ci.cancel();
            }
        }
    }
  }
              
