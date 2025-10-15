package dev.sunglasses.sunnyutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

public class Config {
    public static void copyCoords(Button button) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            var pos = mc.player.blockPosition();
            String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
            mc.keyboardHandler.setClipboard(coords); // copy to clipboard
        }
    }
}
