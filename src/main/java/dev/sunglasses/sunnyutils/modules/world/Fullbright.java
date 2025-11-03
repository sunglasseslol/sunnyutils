package dev.sunglasses.sunnyutils.modules.world;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends ToggleModule {
    public Fullbright() {
        super("Fullbright", GLFW.GLFW_KEY_G);
    }

    @Override
    public void onToggle() {
        // this module ITSELF doesn't actually do much, all it does is set a boolean to enabled / disabled and the MIXIN itself handles the fullbright
        // see dev.sunglasses.sunnyutils.mixin.FullbrightMixin
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) mc.levelRenderer.allChanged();
    }
}