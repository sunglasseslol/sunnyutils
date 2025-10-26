package dev.sunglasses.sunnyutils.modules.hud;

import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.modules.base.GenericModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class WorldData extends GenericModule {
    public WorldData() {
        super("WorldData");
    }

    private final List<Component> displayLines = new ArrayList<>();

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        LocalPlayer player = mc.player;

        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        int playerZ = (int) player.getZ();

        Component xCoords = Component.literal(String.format("%d / ", playerX)).withStyle(ChatFormatting.GREEN);
        Component yCoords = Component.literal(String.format("%d / ", playerY)).withStyle(ChatFormatting.GREEN);
        Component zCoords = Component.literal(String.format("%d /", playerZ)).withStyle(ChatFormatting.GREEN);

        Component coordsText = Component.literal("XYZ: ").append(xCoords).append(yCoords).append(zCoords).append(player.getDirection().toString());


        Holder<Biome> biomeHolder = mc.level.getBiome(player.blockPosition());

        Component biomeText = Component.literal("biome/").append(biomeHolder.unwrapKey().map(key -> key.location().toString())
                .orElse("unknown")).withStyle(ChatFormatting.GREEN);


        long time = mc.level.getDayTime();

        int hours = (int) ((time / 1000 + 6) % 24); // +6 because MC day starts at 6 AM
        int minutes = (int) ((time % 1000) * 60 / 1000);
        Component formattedTime = Component.literal("Time: ").append(String.format("%02d:%02d", hours, minutes)).withStyle(ChatFormatting.GREEN);

        Component fps = Component.literal("FPS: ").append(String.format("%d", mc.getFps())).withStyle(ChatFormatting.GREEN);

        /* ---------------------------------------------------------------------------------- */


        displayLines.clear();
        displayLines.add(coordsText);
        displayLines.add(biomeText);
        displayLines.add(formattedTime);
        displayLines.add(fps);


        // reverse the list (so it's in the right order. coords then biome then time etc etc
        Collections.reverse(displayLines);
    }

    @Override
    public void onRender(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            int offset = 10;
            int lineHeight = mc.font.lineHeight + 1;
            for (Component s : displayLines) {
                Gui.drawString(mc, guiGraphics, s, mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, mc.getWindow().getGuiScaledHeight() - offset, 0xFFFFFFFF);
                offset += lineHeight;
            }
        }
    }
}