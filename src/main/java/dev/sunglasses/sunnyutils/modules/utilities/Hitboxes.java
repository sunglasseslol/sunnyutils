package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Hitboxes extends ToggleModule {
    public Hitboxes() {
        super("Hitboxes", GLFW.GLFW_KEY_J, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
        mc.getEntityRenderDispatcher().setRenderHitBoxes(isEnabled());
    }
}