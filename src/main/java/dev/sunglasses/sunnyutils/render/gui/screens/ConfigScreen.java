package dev.sunglasses.sunnyutils.render.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/*
WIP DOESNT EVEN DO ANYTHING YET 💔
*/

public class ConfigScreen extends Screen {
    private final Screen parent;

    protected ConfigScreen(Screen parent) {
        super(Component.literal("Configuration"));
        this.parent = parent;
    }
w
    @Override
    protected void init() {
        super.init();
        int x = this.width / 2 - 100;
        int y = this.height / 2 - 50;

        this.addRenderableWidget(Button.builder(Component.literal("Back"), b -> {
            this.minecraft.setScreen(parent);
        }).pos(x, y + 70).size(200, 20).build());

        // Add a checkbox or button for your areaMode toggle
        this.addRenderableWidget(
                Checkbox.builder(Component.literal("Area Mode for Ruler"), options.CLIENT.rulerSettings.rulerMode.get())
                        .pos(x, y).size(200, 20)
                        .onPress(cb -> {
                            boolean newVal = !options.CLIENT.rulerSettings.rulerMode.get();
                            options.CLIENT.rulerSettings.rulerMode.set(newVal);
                            cb.setChecked(newVal);
                        }).build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
        super.onClose();
    }
}

