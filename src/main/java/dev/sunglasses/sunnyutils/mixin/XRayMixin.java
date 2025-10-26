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

    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void onSkipRendering(BlockState adjacentState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            BlockBehaviour.BlockStateBase thisState = (BlockBehaviour.BlockStateBase) (Object) this;
            boolean thisIsXRay = XRay.shouldShowBlock(thisState.getBlock());

            // If THIS block is not in the XRay list, skip rendering it entirely
            if (!thisIsXRay) { cir.setReturnValue(true); }
        }
    }

    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void onGetLightEmission(CallbackInfoReturnable<Integer> cir) {
        XRay xray = ModuleManager.getModule(XRay.class);
        if (xray != null && xray.isEnabled()) {
            cir.setReturnValue(15);
        }
    }
}