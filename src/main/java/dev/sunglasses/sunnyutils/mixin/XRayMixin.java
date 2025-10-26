package dev.sunglasses.sunnyutils.mixin;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class XRayMixin {

    /**
     * Make ALL blocks invisible when XRay is enabled
     * We'll manually render colored boxes for the blocks we want to see
     */
    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    private void onGetRenderShape(CallbackInfoReturnable<net.minecraft.world.level.block.RenderShape> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            // Make everything invisible
            cir.setReturnValue(RenderShape.INVISIBLE);
        }
    }

    /**
     * Make everything bright
     */
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void onGetLightEmission(CallbackInfoReturnable<Integer> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            cir.setReturnValue(15);
        }
    }
}