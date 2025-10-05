package dev.sunglasses.sunnyutils.modules;

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

        String coords = "XYZ: " + (int) mc.player.getX() + " / " + (int) mc.player.getY() + " / " + (int) mc.player.getZ();

        guiGraphics.drawString(mc.font, coords, 5, 5, 0xFFFFFFFF);
    }
}