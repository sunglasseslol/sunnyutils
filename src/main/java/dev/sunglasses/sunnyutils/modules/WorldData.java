package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.gui.Gui;
import dev.sunglasses.sunnyutils.modules.base.GenericModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.*;

public class WorldData extends GenericModule {
    public WorldData() {
        super("WorldData");
    }

    private final List<String> displayLines = new ArrayList<>();

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        LocalPlayer player = mc.player;

        int playerX = (int) player.getX();
        int playerY = (int) player.getY();
        int playerZ = (int) player.getZ();

        String coordsText = String.format("XYZ: %d / %d / %d / ", playerX, playerY, playerZ) + player.getDirection();

        Holder<Biome> biomeHolder = mc.level.getBiome(player.blockPosition());
        String biomeText = "biome/" + biomeHolder.unwrapKey()
                .map(key -> key.location().toString())
                .orElse("unknown");


        long time = mc.level.getDayTime();

        int hours = (int) ((time / 1000 + 6) % 24); // +6 because MC day starts at 6 AM
        int minutes = (int) ((time % 1000) * 60 / 1000);
        String formattedTime = String.format("Time: %02d:%02d", hours, minutes);

        String fps = String.format("FPS: %d", mc.getFps());

        String serverIp = null;
        if(mc.getCurrentServer() != null) serverIp = mc.getCurrentServer().ip;
        else if(mc.getCurrentServer() == null) serverIp = "unknown";

        /* ---------------------------------------------------------------------------------- */


        displayLines.clear();
        displayLines.add(coordsText);
        displayLines.add(biomeText);
        displayLines.add(formattedTime);
        displayLines.add(fps);
        displayLines.add(serverIp);


        // reverse the list (so it's in the right order. coords then biome then time etc etc
        Collections.reverse(displayLines);
    }

    @Override
    public void onRender(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            int offset = 10;
            int lineHeight = mc.font.lineHeight + 1;
            for (String s : displayLines) {
                Gui.drawString(mc, guiGraphics, s, mc.getWindow().getGuiScaledWidth() - mc.font.width(s) - 5, mc.getWindow().getGuiScaledHeight() - offset, 0xFFFFFFFF);
                offset += lineHeight;
            }
        }
    }
}