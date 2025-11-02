package dev.sunglasses.sunnyutils.mixin;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.world.XRay;
import net.minecraft.client.Minecraft;
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
            // Don't make blocks invisible if a screen is open (prevents lighting errors)
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
                return; // Let blocks render normally when GUI is open
            }

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
            // Don't affect lighting if a screen is open
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen != null) {
                return; // Let lighting work normally when GUI is open
            }

            cir.setReturnValue(15);
        }
    }
}