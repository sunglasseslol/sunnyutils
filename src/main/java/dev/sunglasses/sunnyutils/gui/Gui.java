package dev.sunglasses.sunnyutils.gui;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import dev.sunglasses.sunnyutils.modules.base.Module;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;

@EventBusSubscriber(modid = SunnyUtils.MODID, value = Dist.CLIENT)
public class Gui {
    /*
    5, 5, = TOP LEFT
    5, mc.getWindow().getGuiScaledHeight(), = BOTTOM LEFT
    mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, 5 = TOP RIGHT
    mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, mc.getWindow().getGuiScaledHeight(), = BOTTOM RIGHT
    */
    public static void drawString(Minecraft mc, GuiGraphics gui, String text, int x, int y, int colour) {
        gui.drawString(mc.font, text, x, y, colour);
    }

    /**
     * Creates a button with given position, size, text, and action.
     */

    public static Button createButton(int x, int y, int width, int height, String text, Button.OnPress onPress) {
        return Button.builder(Component.literal(text), onPress).bounds(x, y, width, height).build();
    }

    /**
     * Creates a button with given text, and action.
     */

    public static Button createButton(String text, Button.OnPress onPress) {
        return Button.builder(Component.literal(text), onPress).build();
    }

    public static void drawModules(Minecraft mc, GuiGraphics gui, List<Module> modules) {
        int startY = 5;                // starting Y position for the first box
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

