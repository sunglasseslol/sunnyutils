package dev.sunglasses.sunnyutils.modules.utilities.submodules;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.Submodule;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import dev.sunglasses.sunnyutils.utils.KeyMappingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class RulerClear extends Submodule {

    public RulerClear() {
        super("RulerClear", GLFW.GLFW_KEY_C, KeyMappingManager.MAIN_CATEGORY, ModuleManager.getModule(Ruler.class));
    }

    @Override
    public void onPress() {
        if(!isParentEnabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null || mc.player == null) return;

        Ruler.clear();
        mc.player.displayClientMessage(Component.literal("Cleared Ruler!"), true);
    }
}
