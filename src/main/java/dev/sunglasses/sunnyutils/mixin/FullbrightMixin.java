package dev.sunglasses.sunnyutils.mixin;
import dev.sunglasses.sunnyutils.modules.utilities.Fullbright;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class FullbrightMixin {
    @Inject(method = "getLightEmission", at = @At("HEAD"), cancellable = true)
    private void onGetLightEmission(CallbackInfoReturnable<Integer> cir) {
        Fullbright fullbright = ModuleManager.getModule(Fullbright.class);
        if (fullbright != null && fullbright.isEnabled()) {
            cir.setReturnValue(15);
        }
    }
}
