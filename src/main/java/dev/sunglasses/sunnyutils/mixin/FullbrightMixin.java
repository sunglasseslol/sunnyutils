package dev.sunglasses.sunnyutils.mixin;
import dev.sunglasses.sunnyutils.modules.Fullbright;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Options.class})
public abstract class FullbrightMixin {
    @Final
    @Shadow
    private OptionInstance<Double> gamma;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Inject(
            method = {"gamma"},
            at = {@At("RETURN")}
    )
    private void Gamma(CallbackInfoReturnable<OptionInstance<Double>> info) {
        Fullbright fb = ModuleManager.getModule(Fullbright.class);
        if (fb != null && fb.isEnabled()) {
            if (gamma.get() != 9999d) gamma.set(9999d);
        } else if (gamma.get() == 9999d) {
            gamma.set(1d);
        }
    }
}
