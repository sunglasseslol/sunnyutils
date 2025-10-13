package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class ConfigScreen extends ButtonModule {
    public ConfigScreen() {
        super("ConfigScreen", GLFW.GLFW_KEY_SEMICOLON, "key.sunnyutils.modules");
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new dev.sunglasses.sunnyutils.gui.screens.ConfigScreen(Component.literal("Config Screen")));
    }
}
