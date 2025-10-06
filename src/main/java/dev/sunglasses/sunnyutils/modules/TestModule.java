package dev.sunglasses.sunnyutils.modules;

import org.lwjgl.glfw.GLFW;

public class TestModule extends ToggleModule {
    public TestModule() {
        super("Test", GLFW.GLFW_KEY_B, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
    }
}