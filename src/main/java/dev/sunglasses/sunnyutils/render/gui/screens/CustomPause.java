package dev.sunglasses.sunnyutils.render.gui.screens;

import dev.sunglasses.sunnyutils.render.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

public class CustomPause extends PauseScreen {
    public CustomPause() {
        super(true);
    }

    @Override
    protected void init() {
        if(minecraft == null) return;
        super.init();
        this.addRenderableWidget(Gui.createButton(minecraft.getWindow().getGuiScaledWidth() - 100 - 5, minecraft.getWindow().getGuiScaledHeight() - 20 - 5, 100, 20, "Quit", (btn) -> minecraft.stop()));
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partial) {
        if(minecraft == null) return;
        super.render(gui, mouseX, mouseY, partial);
    }
}

