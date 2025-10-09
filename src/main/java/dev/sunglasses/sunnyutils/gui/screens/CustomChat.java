package dev.sunglasses.sunnyutils.gui.screens;

import dev.sunglasses.sunnyutils.SunnyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/*
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
TODO : CLEAN UP THIS SHITTY ASS FILE
*/

@EventBusSubscriber(modid = SunnyUtils.MODID)
public class CustomChat {

    private static final int BUTTON_WIDTH = 100;
    private static final int BUTTON_HEIGHT = 20;

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ChatScreen chatScreen)) return;

        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int x = width / 2 - BUTTON_WIDTH / 2;
        int y = height - 30 - BUTTON_HEIGHT - 5;

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        // Hover detection
        boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH &&
                mouseY >= y && mouseY <= y + BUTTON_HEIGHT;

        // Draw button background
        GuiGraphics gui = event.getGuiGraphics();
        // Draw button background
        int bgColor = hovered ? 0xDF000000 : 0x7F000000; // red if hovered, black otherwise
        gui.fill(x, y, x + BUTTON_WIDTH, y + BUTTON_HEIGHT, bgColor);

        // Draw outline if hovered
        if (hovered) {
            int outlineColor = 0xFFFFFFFF; // white outline
            // Top
            gui.fill(x - 1, y - 1, x + BUTTON_WIDTH + 1, y, outlineColor);
            // Bottom
            gui.fill(x - 1, y + BUTTON_HEIGHT, x + BUTTON_WIDTH + 1, y + BUTTON_HEIGHT + 1, outlineColor);
            // Left
            gui.fill(x - 1, y, x, y + BUTTON_HEIGHT, outlineColor);
            // Right
            gui.fill(x + BUTTON_WIDTH, y, x + BUTTON_WIDTH + 1, y + BUTTON_HEIGHT, outlineColor);
        }

        // Draw button text
        gui.drawString(Minecraft.getInstance().font, "Copy Coords", x + (BUTTON_WIDTH - Minecraft.getInstance().font.width("Copy Coords")) / 2,
                y + (BUTTON_HEIGHT - Minecraft.getInstance().font.lineHeight) / 2, 0xFFFFFFFF);


        // Draw button text
        String text = "Copy Coords";
        int textWidth = Minecraft.getInstance().font.width(text);
        int textX = x + (BUTTON_WIDTH - textWidth) / 2;
        int textY = y + (BUTTON_HEIGHT - Minecraft.getInstance().font.lineHeight) / 2;
        gui.drawString(Minecraft.getInstance().font, text, textX, textY, 0xFFFFFFFF);

        // Detect mouse clicks manually
        if (Minecraft.getInstance().mouseHandler.isLeftPressed() && hovered) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var pos = player.blockPosition();
                String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                Minecraft.getInstance().keyboardHandler.setClipboard(coords);
                player.displayClientMessage(Component.literal("Copied coordinates!"), true);
            }
        }
    }
    /** Handle mouse clicks exactly once per click */
    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Post event) {
        if (!(event.getScreen() instanceof ChatScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        int x = width / 2 - BUTTON_WIDTH / 2;
        int y = height - 30 - BUTTON_HEIGHT - 5;

        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();

        boolean hovered = mouseX >= x && mouseX <= x + BUTTON_WIDTH &&
                mouseY >= y && mouseY <= y + BUTTON_HEIGHT;

        if (event.getButton() == 0 && hovered) {
            var player = mc.player;
            if (player != null) {
                var pos = player.blockPosition();
                String coords = pos.getX() + " " + pos.getY() + " " + pos.getZ();
                mc.keyboardHandler.setClipboard(coords);
                player.displayClientMessage(Component.literal("Copied coordinates!"), true);
            }
        }
    }
}
