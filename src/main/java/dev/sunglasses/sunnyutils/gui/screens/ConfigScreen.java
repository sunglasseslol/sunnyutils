package dev.sunglasses.sunnyutils.gui.screens;

import dev.sunglasses.sunnyutils.gui.Config;
import dev.sunglasses.sunnyutils.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class ConfigScreen extends Screen {

    public ConfigScreen(Component title) {
        super(title);
    }

    public void bleh(Button button) {

    }

    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int x = this.width / 2 - buttonWidth / 2;  // center horizontally
        int y = this.height - 30 - buttonHeight;

        this.addRenderableWidget(Gui.createButton(x, y, buttonWidth, buttonHeight, "Copy Coords", Config::copyCoords));
    }
}
