package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.HoldModule;
import org.lwjgl.glfw.GLFW;

public class ShowChat extends HoldModule {
    public ShowChat() {
        super("ShowChat", GLFW.GLFW_KEY_B);
    }
}
