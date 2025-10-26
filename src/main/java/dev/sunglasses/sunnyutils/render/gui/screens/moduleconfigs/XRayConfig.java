package dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs;

import dev.sunglasses.sunnyutils.render.gui.screens.ConfigScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class XRayConfig extends ConfigScreen {
    private final Screen parent;

    public XRayConfig(Screen parent) {
        super(new ConfigScreen(null));
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if (this.minecraft == null) return;
        this.minecraft.setScreen(parent);
        super.onClose();
    }
}
