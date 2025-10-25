package dev.sunglasses.sunnyutils.mixin;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class XRayMixin {

    /**
     * Makes non-XRay blocks not occlude adjacent faces
     */
    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    private void onCanOcclude(CallbackInfoReturnable<Boolean> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;

            if (!XRay.shouldShowBlock(state.getBlock())) {
                cir.setReturnValue(false);
            }
        }
    }

    /**
     * Control face culling - this is what actually hides block faces
     * Returns true to skip rendering, false to render
     */
    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void onSkipRendering(BlockState neighborState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            BlockBehaviour.BlockStateBase thisState = (BlockBehaviour.BlockStateBase) (Object) this;

            boolean thisIsXRay = XRay.shouldShowBlock(thisState.getBlock());
            boolean neighborIsXRay = XRay.shouldShowBlock(neighborState.getBlock());

            // If THIS block is not XRay, skip rendering it (make invisible)
            if (!thisIsXRay) {
                cir.setReturnValue(true);
                return;
            }

            // If THIS block IS XRay:
            // - Always show faces next to non-XRay blocks (so you can see the ore)
            // - Show faces next to air/empty
            // - Can hide faces between two XRay blocks (optional, for cleaner look)
            if (thisIsXRay && !neighborIsXRay) {
                cir.setReturnValue(false); // Always render faces next to non-XRay blocks
            }
        }
    }

    /**
     * Makes non-XRay blocks not cast shadows
     */
    @Inject(method = "useShapeForLightOcclusion", at = @At("HEAD"), cancellable = true)
    private void onUseShapeForLightOcclusion(CallbackInfoReturnable<Boolean> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;

            if (!XRay.shouldShowBlock(state.getBlock())) {
                cir.setReturnValue(true);
            }
        }
    }

    /**
     * Make everything bright
     */
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void onGetLightEmission(CallbackInfoReturnable<Integer> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;

            if (!XRay.shouldShowBlock(state.getBlock())) {
                cir.setReturnValue(15);
            }
        }
    }
}