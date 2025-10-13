package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;

public class ClearChat extends ButtonModule {

    public ClearChat() {
        super("ClearChat", GLFW.GLFW_KEY_U, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(true);
    }
}
