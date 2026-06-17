package com.mobilecull.mixin;

import com.mobilecull.engine.MobileCullEngine;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    private <E extends BlockEntity> void onRender(
        E blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci
    ) {
        if (!MobileCullEngine.INSTANCE.shouldRender(blockEntity)) {
            ci.cancel();
        }
    }
}
