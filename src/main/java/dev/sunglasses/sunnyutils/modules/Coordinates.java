package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.gui.Gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class Coordinates extends GenericModule {
    public Coordinates() {
        super("Coordinates");
    }

    @Override
    public void onRender(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        String coords = "XYZ: " + (int) mc.player.getX() + " / " + (int) mc.player.getY() + " / " + (int) mc.player.getZ() + " / " + mc.player.getDirection();

        int y = mc.getWindow().getGuiScaledHeight() - 10 - mc.font.lineHeight - 2;
        Gui.drawString(mc, guiGraphics, coords, 5, y, 0xFFFFFFFF);
    }
}