package dev.sunglasses.sunnyutils.modules.utilities.submodules;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.Submodule;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class RulerAreaToggle extends Submodule {

    public RulerAreaToggle() {
        super("RulerAreaToggle", GLFW.GLFW_KEY_X, "key.sunnyutils.modules", ModuleManager.getModule(Ruler.class));
    }

    @Override
    public void onPress() {
        if(!isParentEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        mc.gui.getChat().clearMessages(true);
    }
}
