package dev.sunglasses.sunnyutils.render.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;
        super.init();
        int topRightX = this.minecraft.getWindow().getGuiScaledWidth() - 200 - 5;
        int topRightY = this.minecraft.getWindow().getGuiScaledHeight() - 5;

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            this.minecraft.setScreen(parent);
        }).pos(topRightX, topRightY).size(200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(parent);
        super.onClose();
    }
}

