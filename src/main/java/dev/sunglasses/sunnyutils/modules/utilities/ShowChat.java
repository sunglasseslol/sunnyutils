package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.HoldModule;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ShowChat extends HoldModule {
    public ShowChat() {
        super("ShowChat", GLFW.GLFW_KEY_B, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;
    }

    @Override
    public void onRelease() {

    }
}
