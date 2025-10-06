package dev.sunglasses.sunnyutils.modules;

import org.lwjgl.glfw.GLFW;

public class Hitboxes extends ToggleModule {
    public Hitboxes() {
        super("Hitboxes", GLFW.GLFW_KEY_B, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
    }
}