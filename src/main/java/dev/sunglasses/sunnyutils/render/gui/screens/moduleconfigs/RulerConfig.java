package dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs;

import dev.sunglasses.sunnyutils.render.gui.screens.ConfigScreen;
import net.minecraft.client.gui.GuiGraphics;

public class RulerConfig extends ConfigScreen {
    public RulerConfig() {
        super(new ConfigScreen(null, "Configuration"), "Ruler");
    }

    @Override
    protected void init() {
        if (this.minecraft == null) return;
        //



        //
        this.addRenderableWidget(drawBackButton(this.minecraft));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null) return;
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
