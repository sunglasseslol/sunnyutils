package dev.sunglasses.sunnyutils.gui;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.gui.screens.CustomChat;
import dev.sunglasses.sunnyutils.modules.ToggleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import dev.sunglasses.sunnyutils.modules.Module;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;

@EventBusSubscriber(modid = SunnyUtils.MODID, value = Dist.CLIENT)
public class Gui {
    /*
    x = 5 ; y = 5 = TOP LEFT
    x = 5 ; y = .getGuiScaledHeight() = BOTTOM LEFT
    x = 5 ; y = 5 = TOP RIGHT
    x = 5 ; y = .getGuiScaledHeight() = BOTTOM RIGHT
    */
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

