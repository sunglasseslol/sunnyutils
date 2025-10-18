package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ChunkBorders extends ToggleModule {
    public ChunkBorders() { super("ChunkBorders", GLFW.GLFW_KEY_N, "key.sunnyutils.modules"); }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
        mc.debugRenderer.switchRenderChunkborder();
    }
}
