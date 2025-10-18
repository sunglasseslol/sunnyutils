package dev.sunglasses.sunnyutils.gui.screens;

import dev.sunglasses.sunnyutils.gui.Config;
import dev.sunglasses.sunnyutils.gui.Gui;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

/*
WIP DOESNT EVEN DO ANYTHING YET 💔
*/

public class UtilityScreen extends Screen {

    public UtilityScreen(Component title) {
        super(title);
    }
    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        this.addRenderableWidget(Gui.createButton(
                5,
                mc.getWindow().getGuiScaledHeight() - 25,
                100,
                20,
                "Back",
                (btn) -> {
                    mc.setScreen(null);
                }
        ));
    }
}
