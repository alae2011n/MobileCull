package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void cullBlockEntityRender(
        E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci
    ) {
        MobileCullEngine engine = MobileCullEngine.INSTANCE;
        if (engine.cullBlockEntities) {
            double x = blockEntity.getPos().getX();
            double y = blockEntity.getPos().getY();
            double z = blockEntity.getPos().getZ();
            
            boolean visible = engine.isBoxVisible(
                x, y, z,
                x + 1.0, y + 1.0, z + 1.0
            );

            if (!visible) {
                ci.cancel();
            }
        }
    }
}
