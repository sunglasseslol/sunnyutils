package dev.sunglasses.sunnyutils.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class ConfigScreen extends Screen {

    public ConfigScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;  // center horizontally
        int y = this.height - 30 - buttonHeight;

        this.addRenderableWidget(Button.builder(Component.literal("Copy Coords"), (btn) -> {
            if (mc.player != null) {
                var pos = mc.player.blockPosition();
                String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                mc.keyboardHandler.setClipboard(coords); // copy to clipboard
            }
        }).bounds(x, y, buttonWidth, buttonHeight).build());
    }
}
