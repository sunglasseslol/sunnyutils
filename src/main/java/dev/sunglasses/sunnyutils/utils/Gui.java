package dev.sunglasses.sunnyutils.utils;

import dev.sunglasses.sunnyutils.modules.ToggleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import dev.sunglasses.sunnyutils.modules.Module;

import java.util.List;

public class Gui {
    public static void drawString(Minecraft mc, GuiGraphics gui, String text, int x, int y, int colour) {
        gui.drawString(mc.font, text, x, y, colour);
    }

    public static void drawModules(Minecraft mc, GuiGraphics gui, List<Module> modules) {
        int y = 5;
        for(Module m : modules) {
            if(m instanceof ToggleModule toggle && toggle.isEnabled()) {
                drawString(mc, gui, m.getName(), 5, y, 0xFFFFFFFF);
                y += mc.font.lineHeight + 2;
            }
        }
    }
}
