package dev.sunglasses.sunnyutils.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;

public class CustomChat extends ChatScreen {
    public CustomChat() {
        super("");
    }

    @Override
    public void init() {
        super.init();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;  // center horizontally
        int y = this.height - 30 - buttonHeight;   // just above input

        this.addRenderableWidget(Button.builder(Component.literal("Copy Coords"), (btn) -> {
            if (minecraft.player != null) {
                var pos = minecraft.player.blockPosition();
                String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                minecraft.keyboardHandler.setClipboard(coords); // copy to clipboard
                minecraft.player.displayClientMessage(Component.literal("Copied: " + coords), true);
            }
        }).bounds(x, y, buttonWidth, buttonHeight).build());
    }
}
