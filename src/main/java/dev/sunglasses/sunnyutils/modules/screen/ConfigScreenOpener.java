package dev.sunglasses.sunnyutils.modules.screen;

import dev.sunglasses.sunnyutils.gui.screens.ConfigScreen;
import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ConfigScreenOpener extends ButtonModule {
    public ConfigScreenOpener() {
        super("ConfigScreenOpener", GLFW.GLFW_KEY_SEMICOLON, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new ConfigScreen(Component.literal("Config Screen")));
    }
}
