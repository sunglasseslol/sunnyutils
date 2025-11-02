package dev.sunglasses.sunnyutils.mixin;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.ShowChat;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public abstract class ShowChatMixin {
    @Inject(method = "getTimeFactor", at = @At("HEAD"), cancellable = true)
    private static void forceShowChat(int counter, CallbackInfoReturnable<Double> cir) {
        ShowChat module = ModuleManager.getModule(ShowChat.class);
        if (module != null && module.isHeld()) {
            // Force chat to be fully visible (1.0 = 100% opacity)
            cir.setReturnValue(1.0);
        }
    }
}