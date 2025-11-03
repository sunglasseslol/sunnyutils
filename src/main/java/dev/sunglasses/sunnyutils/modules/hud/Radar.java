package dev.sunglasses.sunnyutils.modules.hud;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ButtonModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Radar extends ButtonModule {
    public Radar() {
        super("Radar", GLFW.GLFW_KEY_P);
    }

    public static List<AbstractClientPlayer> getPlayersInRenderDistance() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer localPlayer = mc.player;

        if (mc.level == null || localPlayer == null) {
            return new ArrayList<>();
        }

        List<AbstractClientPlayer> nearbyPlayers = new ArrayList<>();

        // Get render distance in blocks
        int renderDistance = mc.options.renderDistance().get() * 16; // chunks * 16 blocks

        // Create bounding box around player
        Vec3 playerPos = localPlayer.position();
        AABB searchBox = new AABB(
                playerPos.x - renderDistance,
                mc.level.getMinY(),
                playerPos.z - renderDistance,
                playerPos.x + renderDistance,
                mc.level.getMaxY(),
                playerPos.z + renderDistance
        );

        // Get all client players in the area
        List<AbstractClientPlayer> allPlayers = mc.level.players();
        for (AbstractClientPlayer player : allPlayers) {
            // Skip yourself
            if (player == localPlayer) continue;

            // Check if player is within the bounding box
            if (searchBox.contains(player.position())) {
                nearbyPlayers.add(player);
            }
        }

        return nearbyPlayers;
    }

    private void printPlayerInfo() {
        List<AbstractClientPlayer> plrs = getPlayersInRenderDistance();
        for(Player player : plrs) {
            // Get player info
            String name = player.getName().getString();
            Vec3 pos = player.position();
            double health = player.getHealth();
            boolean isSneaking = player.isCrouching();
            boolean isFlying = player.getAbilities().flying;

            System.out.println("Player: " + name);
            System.out.println("  Position: " + pos.x + ", " + pos.y + ", " + pos.z);
            System.out.println("  Health: " + health);
            System.out.println("  Sneaking: " + isSneaking);
            System.out.println("  Flying: " + isFlying);

            // Get what they're holding
            System.out.println("  Holding: " + player.getMainHandItem().getDisplayName().getString());

            // Get distance from you
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                double distance = player.distanceTo(mc.player);
                System.out.println("  Distance: " + String.format("%.1f", distance) + " blocks");
            }
        }
    }

    @Override
    public void onPress() {
        Minecraft mc = Minecraft.getInstance();
        if(mc == null) return;
        System.out.println("test");
        SunnyUtils.LOGGER.info("TEST 2");
        printPlayerInfo();
    }
}
