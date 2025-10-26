package dev.sunglasses.sunnyutils.render.gui.screens;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.render.gui.Config;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs.XRayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final Component title;

    public ConfigScreen(Screen parent, Component title) {
        super(title);
        this.title = title;
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;
        super.init();
        this.addRenderableWidget(drawBackButton(this.minecraft));

        this.addRenderableWidget(Gui.openScreenButton(this.width / 2, this.height / 2, 100, 20, "XRay", () -> new XRayConfig(this, Component.literal("XRay"))));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;
        this.renderMenuBackground(graphics);

        graphics.drawCenteredString(this.minecraft.font, this.title, this.width/2, 20, 0xFFFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(parent);
        super.onClose();
    }

    public Button drawBackButton(Minecraft mc) {
        return Gui.createButton(mc.getWindow().getGuiScaledWidth() - 100 - 5, 5, 100, 20, "Back", (btn) -> this.onClose());
    }
}

