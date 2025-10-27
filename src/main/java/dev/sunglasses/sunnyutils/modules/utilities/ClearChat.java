package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;

public class ClearChat extends ButtonModule {

    public ClearChat() {
        super("ClearChat", GLFW.GLFW_KEY_U, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        mc.gui.getChat().clearMessages(true);
        mc.player.displayClientMessage(Component.literal("Cleared chat!"), true);
    }
}
