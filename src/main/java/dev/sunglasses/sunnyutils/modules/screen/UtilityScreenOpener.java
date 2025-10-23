package dev.sunglasses.sunnyutils.modules.screen;

import dev.sunglasses.sunnyutils.render.gui.screens.UtilityScreen;
import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class UtilityScreenOpener extends ButtonModule {
    public UtilityScreenOpener() {
        super("UtilityScreenOpener", GLFW.GLFW_KEY_APOSTROPHE, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new UtilityScreen(Component.literal("Utility Screen")));
    }
}
