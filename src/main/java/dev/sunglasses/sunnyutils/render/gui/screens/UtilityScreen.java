package dev.sunglasses.sunnyutils.render.gui.screens;

import dev.sunglasses.sunnyutils.render.gui.Gui;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class UtilityScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);

    public UtilityScreen(Component title) {
        super(title);
    }
    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        this.addRenderableWidget(Gui.createButton(
                mc.getWindow().getGuiScaledWidth()- 5 - 100,
                5,
                100,
                20,
                "Back",
                (btn) -> {
                    mc.setScreen(null);
                }
        ));
    }
}
