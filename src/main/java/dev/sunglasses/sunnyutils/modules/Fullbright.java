package dev.sunglasses.sunnyutils.modules;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends ToggleModule {
    public Fullbright() {
        super("Fullbright", GLFW.GLFW_KEY_K, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
        if (isEnabled()) System.out.println("Fullbright enabled!");
        else System.out.println("Fullbright disabled!");
    }
}