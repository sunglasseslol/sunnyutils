package dev.sunglasses.sunnyutils.utils;

import dev.sunglasses.sunnyutils.SunnyUtils;
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
        int startY = 5;               // starting Y position for the first box
        int gap = 3;                   // pixels between boxes
        int padding = 6;               // vertical padding inside each box
        int boxHeight = mc.font.lineHeight + padding * 2;

        int y = startY;

        for (Module m : modules) {
            if (m instanceof ToggleModule toggle && toggle.isEnabled()) {
                int boxMinY = y;
                int boxMaxY = boxMinY + boxHeight;

                // background box
                gui.fill( 5, boxMinY, mc.font.width(m.getName()) + 25, boxMaxY, 0x7F000000);

                // vertically centered text
                int textY = boxMinY + (boxHeight - mc.font.lineHeight) / 2;
                drawString(mc, gui, m.getName(), 15, textY, 0xFFFFFFFF);

                // move down for next box
                y += boxHeight + gap;
            }
        }
    }

}

