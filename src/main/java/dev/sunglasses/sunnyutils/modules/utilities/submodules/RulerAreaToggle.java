package dev.sunglasses.sunnyutils.modules.utilities.submodules;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.Submodule;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class RulerAreaToggle extends Submodule {

    public RulerAreaToggle() {
        super("RulerAreaToggle", GLFW.GLFW_KEY_Z, ModuleManager.getModule(Ruler.class));
    }

    @Override
    public void onPress() {
        if(!isParentEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null) return;

        Ruler.changeAreaMode();
        mc.player.displayClientMessage(Component.literal("Area Mode: " + (Ruler.getAreaMode() ? "ON" : "OFF")), true);
    }
}
