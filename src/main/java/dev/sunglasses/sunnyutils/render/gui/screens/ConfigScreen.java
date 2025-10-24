package dev.sunglasses.sunnyutils.render.gui.screens;

import com.mojang.datafixers.types.templates.Check;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.utils.Options;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

/*
WIP DOESNT EVEN DO ANYTHING YET 💔
*/

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 50;

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            this.minecraft.setScreen(parent);
        }).pos(x, y + 70).size(200, 20).build());

        this.addRenderableWidget(Gui.createCycleButton(
                x, y, 100, 20,
                "Area Mode",
                (btn, value) -> {
                    Ruler.setAreaMode(value);
                    // Optional: save config or show message
                },
                Ruler.getAreaMode()
        ));

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
        super.onClose();
    }
}

