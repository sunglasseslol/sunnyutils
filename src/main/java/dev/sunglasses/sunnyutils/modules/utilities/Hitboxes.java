package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.utils.KeyMappingManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Hitboxes extends ToggleModule {
    public Hitboxes() {
        super("Hitboxes", GLFW.GLFW_KEY_J, KeyMappingManager.MAIN_CATEGORY);
    }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
//        mc.getEntityRenderDispatcher()
    }
}