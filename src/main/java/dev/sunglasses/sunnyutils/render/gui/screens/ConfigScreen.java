package dev.sunglasses.sunnyutils.render.gui.screens;

import dev.sunglasses.sunnyutils.render.gui.Config;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

/*
WIP DOESNT EVEN DO ANYTHING YET 💔
*/

public class ConfigScreen extends Screen {

    public ConfigScreen(Component title) {
        super(title);
    }
    @Override
    protected void init() {
        Minecraft mc = Minecraft.getInstance();

        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
        GridLayout gridlayout = new GridLayout();
        gridlayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper gridlayout$rowhelper = gridlayout.createRowHelper(1);
        gridlayout$rowhelper.addChild(Gui.createButton("Copy Coords", Config::copyCoords));

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

        layout.addToContents(gridlayout);
        layout.arrangeElements();
    }
}
