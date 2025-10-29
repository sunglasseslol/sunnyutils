package dev.sunglasses.sunnyutils.render.gui.screens;

import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.utilities.Ruler;
import dev.sunglasses.sunnyutils.modules.utilities.Scaffold;
import dev.sunglasses.sunnyutils.modules.utilities.XRay;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs.RulerConfig;
import dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs.ScaffoldConfig;
import dev.sunglasses.sunnyutils.render.gui.screens.moduleconfigs.XRayConfig;
import dev.sunglasses.sunnyutils.utils.ButtonData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private final String title;

    public ConfigScreen(Screen parent, String title) {
        super(Component.literal(title));
        this.title = title;
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        if (this.minecraft == null) return;
        ModuleManager mm = ModuleManager.get();

        // --- Create the grid ---
        GridLayout grid = new GridLayout();
        grid.defaultCellSetting()
                .padding(4)
                .alignHorizontallyCenter()
                .alignVerticallyMiddle();

        GridLayout.RowHelper rowHelper = grid.createRowHelper(2);

        // Define your button data
        List<ButtonData> buttons = List.of(
                new ButtonData(mm.getModule(XRay.class).getName(), () -> new XRayConfig()),
                new ButtonData(mm.getModule(Ruler.class).getName(), () -> new RulerConfig()),
                new ButtonData(mm.getModule(Scaffold.class).getName(), () -> new ScaffoldConfig())
        );

        // Add buttons to grid
        for (ButtonData data : buttons) {
            Button btn = Gui.openScreenButton(0, 0, data.name, data.screenSupplier);
            rowHelper.addChild(btn);
        }

        // Add a Back button at the bottom

        // --- Center the grid on the screen ---
        grid.arrangeElements();
        grid.setX(this.width / 5 - grid.getWidth() / 2);
        grid.setY(100);

        // --- Actually add to the screen ---
        grid.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(drawBackButton(this.minecraft));
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
    }

    public Button drawBackButton(Minecraft mc) {
        return Gui.createButton(mc.getWindow().getGuiScaledWidth() - 100 - 5, 5, 100, 20, "Back", (btn) -> this.onClose());
    }
}