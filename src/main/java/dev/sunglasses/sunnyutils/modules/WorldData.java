package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.gui.Gui;
import dev.sunglasses.sunnyutils.modules.base.GenericModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public class WorldData extends GenericModule {
    public WorldData() {
        super("WorldData");
    }

    @Override
    public void onRender(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            LocalPlayer player = mc.player;
            int playerX = (int) player.getX();
            int playerY = (int) player.getY();
            int playerZ = (int) player.getZ();

            String coords = "XYZ: " + playerX + " / " + playerY + " / " + playerZ + " / " + mc.player.getDirection();

            Holder<Biome> biomeHolder = mc.level.getBiome(player.blockPosition());
            String biome = "biome/" + biomeHolder.unwrapKey()
                    .map(key -> key.location().toString())
                    .orElse("unknown");


            Gui.drawString(mc, guiGraphics, coords, 5, mc.getWindow().getGuiScaledHeight() - 10, 0xFFFFFFFF);
            Gui.drawString(mc, guiGraphics, biome, 5, mc.getWindow().getGuiScaledHeight() - 10 - mc.font.lineHeight, 0xFFFFFFFF);
        }
    }
}