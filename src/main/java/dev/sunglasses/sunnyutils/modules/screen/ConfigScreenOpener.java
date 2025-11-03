package dev.sunglasses.sunnyutils.modules.screen;

import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import dev.sunglasses.sunnyutils.render.gui.screens.ConfigScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ConfigScreenOpener extends ButtonModule {
    public ConfigScreenOpener() {
        super("ConfigScreenOpener", GLFW.GLFW_KEY_SEMICOLON);
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfigScreen(null, "Configuration"));
    }
}
