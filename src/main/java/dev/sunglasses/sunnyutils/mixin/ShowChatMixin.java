package dev.sunglasses.sunnyutils.mixin;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.ShowChat;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public abstract class ShowChatMixin {
    @Inject(method = "isChatFocused", at = @At("HEAD"), cancellable = true)
    private void isChatFocused(CallbackInfoReturnable<Boolean> cir) {
        ShowChat module = ModuleManager.getModule(ShowChat.class);
        if(module == null) return;
        if(module.isHeld()) {
            cir.setReturnValue(true);
            SunnyUtils.LOGGER.info("HELD");
        } else {
            cir.setReturnValue(false);
        }
    }
}
