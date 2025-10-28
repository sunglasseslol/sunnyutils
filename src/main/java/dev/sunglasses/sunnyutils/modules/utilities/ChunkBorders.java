package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.utils.KeyMappingManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ChunkBorders extends ToggleModule {
    public ChunkBorders() { super("ChunkBorders", GLFW.GLFW_KEY_N, KeyMappingManager.MAIN_CATEGORY); }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
//        mc.getDebugOverlay();
    }
}
