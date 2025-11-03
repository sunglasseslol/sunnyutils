package dev.sunglasses.sunnyutils.modules.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.render.Renderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@EventBusSubscriber(modid = SunnyUtils.MODID)
public class XRay extends ToggleModule {

    // Map of blocks to their colors (RGBA)
    private static final Map<Block, float[]> DEFAULT_XRAY_BLOCKS = new HashMap<>();

    // User's custom whitelist
    private static Set<Block> whitelistedBlocks = new HashSet<>();

    // User's custom colors for blocks (stored as hex color integers)
    private static Map<Block, Integer> blockColors = new HashMap<>();

    // Config file paths
    private static final Path CONFIG_PATH = Paths.get("config", "sunnyutils", "xray_whitelist.json");
    private static final Path COLORS_CONFIG_PATH = Paths.get("config", "sunnyutils", "xray_colors.json");

    static {
        // Default blocks (these are shown even if not in whitelist for backwards compatibility)
        // Diamonds - Cyan
        DEFAULT_XRAY_BLOCKS.put(Blocks.DIAMOND_ORE, new float[]{0.0f, 1.0f, 1.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_DIAMOND_ORE, new float[]{0.0f, 1.0f, 1.0f, 0.8f});

        // Coal - Black
        DEFAULT_XRAY_BLOCKS.put(Blocks.COAL_ORE, new float[]{0.0f, 0.0f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_COAL_ORE, new float[]{0.0f, 0.0f, 0.0f, 0.8f});

        // Emeralds - Green
        DEFAULT_XRAY_BLOCKS.put(Blocks.EMERALD_ORE, new float[]{0.0f, 1.0f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_EMERALD_ORE, new float[]{0.0f, 1.0f, 0.0f, 0.8f});

        // Gold - Yellow
        DEFAULT_XRAY_BLOCKS.put(Blocks.GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.NETHER_GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});

        // Iron - Light Gray
        DEFAULT_XRAY_BLOCKS.put(Blocks.IRON_ORE, new float[]{0.8f, 0.8f, 0.8f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_IRON_ORE, new float[]{0.8f, 0.8f, 0.8f, 0.8f});

        // Lapis - Blue
        DEFAULT_XRAY_BLOCKS.put(Blocks.LAPIS_ORE, new float[]{0.2f, 0.2f, 1.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_LAPIS_ORE, new float[]{0.2f, 0.2f, 1.0f, 0.8f});

        // Redstone - Red
        DEFAULT_XRAY_BLOCKS.put(Blocks.REDSTONE_ORE, new float[]{1.0f, 0.0f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_REDSTONE_ORE, new float[]{1.0f, 0.0f, 0.0f, 0.8f});

        // Copper - Orange
        DEFAULT_XRAY_BLOCKS.put(Blocks.COPPER_ORE, new float[]{1.0f, 0.5f, 0.0f, 0.8f});
        DEFAULT_XRAY_BLOCKS.put(Blocks.DEEPSLATE_COPPER_ORE, new float[]{1.0f, 0.5f, 0.0f, 0.8f});

        // Ancient Debris - Purple
        DEFAULT_XRAY_BLOCKS.put(Blocks.ANCIENT_DEBRIS, new float[]{0.6f, 0.0f, 0.6f, 0.8f});

        // Nether Quartz - White
        DEFAULT_XRAY_BLOCKS.put(Blocks.NETHER_QUARTZ_ORE, new float[]{1.0f, 1.0f, 1.0f, 0.8f});
    }

    // Default blocks to add to whitelist on first launch
    private static final List<Block> DEFAULT_WHITELIST = List.of(
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.ANCIENT_DEBRIS
    );

    // Cache ore positions to avoid rescanning every frame
    private static final Map<BlockPos, float[]> oreCache = new HashMap<>();
    private static int scanTicker = 0;
    private static BlockPos lastPlayerChunk = null;

    // Configurable settings
    private static int scanDelay = 40; // ticks between scans (default 2 seconds)
    private static int chunkRadius = 3; // chunks to scan around player
    private static int renderDistance = 64; // max blocks to render ores

    // Delayed chunk reload system
    private static int reloadDelay = 0;
    private static boolean needsReload = false;

    public XRay() {
        super("XRay", GLFW.GLFW_KEY_X);
        loadWhitelist(); // Load whitelist when module is created
    }

    // Getters and setters for configuration
    public static int getScanDelay() { return scanDelay; }
    public static void setScanDelay(int delay) { scanDelay = Math.max(10, Math.min(200, delay)); }

    public static int getChunkRadius() { return chunkRadius; }
    public static void setChunkRadius(int radius) { chunkRadius = Math.max(1, Math.min(10, radius)); }

    public static int getRenderDistance() { return renderDistance; }
    public static void setRenderDistance(int distance) { renderDistance = Math.max(16, Math.min(256, distance)); }

    // Whitelist management
    public static Set<Block> getWhitelistedBlocks() {
        return new HashSet<>(whitelistedBlocks);
    }

    public static void setWhitelistedBlocks(Set<Block> blocks) {
        whitelistedBlocks = new HashSet<>(blocks);
        System.out.println("Whitelist updated with " + whitelistedBlocks.size() + " blocks");
    }

    public static void addBlockToWhitelist(Block block) {
        whitelistedBlocks.add(block);
    }

    public static void removeBlockFromWhitelist(Block block) {
        whitelistedBlocks.remove(block);
    }

    // Color management
    public static Integer getBlockColor(Block block) {
        return blockColors.get(block);
    }

    public static void setBlockColor(Block block, int color) {
        blockColors.put(block, color);
    }

    public static Map<Block, Integer> getAllBlockColors() {
        return new HashMap<>(blockColors);
    }

    // Convert color integer (0xRRGGBB) to float array for rendering
    private static float[] colorToFloatArray(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new float[]{r, g, b, 0.8f};
    }

    // Get the color for a block (custom or default)
    private static float[] getColorForBlock(Block block) {
        // Check if user has set a custom color
        if (blockColors.containsKey(block)) {
            return colorToFloatArray(blockColors.get(block));
        }

        // Fall back to default color if available
        if (DEFAULT_XRAY_BLOCKS.containsKey(block)) {
            return DEFAULT_XRAY_BLOCKS.get(block);
        }

        // Default white if no color set
        return new float[]{1.0f, 1.0f, 1.0f, 0.8f};
    }

    // Save whitelist to config file
    public static void saveWhitelist() {
        try {
            // Create config directory if it doesn't exist
            Files.createDirectories(CONFIG_PATH.getParent());

            // Convert blocks to resource location strings
            List<String> blockIds = new ArrayList<>();
            for (Block block : whitelistedBlocks) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                blockIds.add(id.toString());
            }

            // Write whitelist to file as JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
                gson.toJson(blockIds, writer);
            }

            System.out.println("Saved " + blockIds.size() + " blocks to whitelist config");
        } catch (IOException e) {
            System.err.println("Failed to save XRay whitelist: " + e.getMessage());
            e.printStackTrace();
        }

        // Also save colors
        saveColors();
    }

    // Save custom block colors
    private static void saveColors() {
        try {
            Files.createDirectories(COLORS_CONFIG_PATH.getParent());

            // Convert colors to JSON format: {"minecraft:diamond_ore": "00FFFF", ...}
            Map<String, String> colorMap = new HashMap<>();
            for (Map.Entry<Block, Integer> entry : blockColors.entrySet()) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(entry.getKey());
                String hexColor = String.format("%06X", entry.getValue() & 0xFFFFFF);
                colorMap.put(id.toString(), hexColor);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(COLORS_CONFIG_PATH.toFile())) {
                gson.toJson(colorMap, writer);
            }

            System.out.println("Saved " + colorMap.size() + " custom colors");
        } catch (IOException e) {
            System.err.println("Failed to save XRay colors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load whitelist from config file
    public static void loadWhitelist() {
        whitelistedBlocks.clear();

        if (!Files.exists(CONFIG_PATH)) {
            System.out.println("No XRay whitelist config found, creating with defaults");
            // First launch - add default blocks
            whitelistedBlocks.addAll(DEFAULT_WHITELIST);
            saveWhitelist(); // Save the defaults
            return;
        }

        try {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
                List<String> blockIds = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());

                if (blockIds != null) {
                    for (String id : blockIds) {
                        try {
                            ResourceLocation resourceLocation = ResourceLocation.parse(id);
                            Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.get(resourceLocation);
                            if (holder.isPresent()) {
                                Block block = holder.get().value();
                                if (block != null && block != Blocks.AIR) {
                                    whitelistedBlocks.add(block);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to load block: " + id);
                        }
                    }
                }
            }

            System.out.println("Loaded " + whitelistedBlocks.size() + " blocks from whitelist config");
        } catch (Exception e) {
            System.err.println("Failed to load XRay whitelist: " + e.getMessage());
            e.printStackTrace();
        }

        // Also load custom colors
        loadColors();
    }

    // Load custom block colors
    private static void loadColors() {
        blockColors.clear();

        if (!Files.exists(COLORS_CONFIG_PATH)) {
            System.out.println("No XRay colors config found");
            return;
        }

        try {
            Gson gson = new Gson();
            try (FileReader reader = new FileReader(COLORS_CONFIG_PATH.toFile())) {
                Map<String, String> colorMap = gson.fromJson(reader, new TypeToken<Map<String, String>>(){}.getType());

                if (colorMap != null) {
                    for (Map.Entry<String, String> entry : colorMap.entrySet()) {
                        try {
                            ResourceLocation resourceLocation = ResourceLocation.parse(entry.getKey());
                            Optional<Holder.Reference<Block>> holder = BuiltInRegistries.BLOCK.get(resourceLocation);
                            if (holder.isPresent()) {
                                Block block = holder.get().value();
                                int color = Integer.parseInt(entry.getValue(), 16);
                                blockColors.put(block, color);
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to load color for block: " + entry.getKey());
                        }
                    }
                }
            }

            System.out.println("Loaded " + blockColors.size() + " custom colors");
        } catch (Exception e) {
            System.err.println("Failed to load XRay colors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onToggle() {
        // Clear cache immediately
        oreCache.clear();
        lastPlayerChunk = null;

        // Schedule chunk reload after 5 ticks (250ms)
        // This gives the screen time to fully close
        needsReload = true;
        reloadDelay = 5;

        System.out.println("XRay toggled: " + (isEnabled() ? "ON" : "OFF"));
    }

    @Override
    public void onTick() {
        // Handle delayed chunk reload
        if (needsReload && reloadDelay > 0) {
            reloadDelay--;
            if (reloadDelay == 0) {
                needsReload = false;
                Minecraft mc = Minecraft.getInstance();
                if (mc.level != null && mc.levelRenderer != null && mc.screen == null) {
                    try {
                        mc.levelRenderer.allChanged();
                        System.out.println("Chunks reloaded for XRay");
                    } catch (Exception e) {
                        System.err.println("Error reloading chunks: " + e.getMessage());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {

        Minecraft mc = Minecraft.getInstance();
        XRay xray = ModuleManager.getModule(XRay.class);
        if (mc.level == null || mc.player == null || xray == null || !xray.isEnabled()) return;

        // Don't scan or render if the world is being saved or if server is stopping
        if (mc.isSameThread() && mc.level.getServer() != null && mc.level.getServer().isStopped()) {
            return;
        }

        BlockPos playerPos = mc.player.blockPosition();
        BlockPos playerChunk = new BlockPos(playerPos.getX() >> 4, 0, playerPos.getZ() >> 4);

        // Rescan every scanDelay ticks or if player moved to new chunk
        scanTicker++;
        if (scanTicker >= getScanDelay() || !playerChunk.equals(lastPlayerChunk)) {
            scanTicker = 0;
            lastPlayerChunk = playerChunk;

            // Only scan if not saving - add safety check
            try {
                scanForOres(mc, playerPos);
            } catch (Exception e) {
                System.err.println("Error during XRay scan: " + e.getMessage());
                // Clear cache on error to prevent repeated failures
                oreCache.clear();
            }
        }

        // Render cached ores
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(Renderer.SOLID_BOX);
        Matrix4f matrix = poseStack.last().pose();

        // Only render ores within render distance
        double maxDistSq = Math.pow(getRenderDistance(), 2);
        for (Map.Entry<BlockPos, float[]> entry : oreCache.entrySet()) {
            if (entry.getKey().distSqr(playerPos) > maxDistSq) continue;

            AABB box = new AABB(entry.getKey());
            float[] color = entry.getValue();
            Renderer.renderBox(consumer, matrix, box, color[0], color[1], color[2], color[3]);
        }

        poseStack.popPose();
        bufferSource.endBatch(Renderer.SOLID_BOX);
    }

    private static void scanForOres(Minecraft mc, BlockPos playerPos) {
        oreCache.clear();

        // Safety checks
        if (mc.level == null || playerPos == null) {
            return;
        }

        int chunkRadius = getChunkRadius();
        int minY = Math.max(mc.level.getMinY(), playerPos.getY() - 64);
        int maxY = Math.min(mc.level.getMaxY(), playerPos.getY() + 64);

        for (int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
            for (int chunkZ = -chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
                int worldChunkX = (playerPos.getX() >> 4) + chunkX;
                int worldChunkZ = (playerPos.getZ() >> 4) + chunkZ;

                try {
                    LevelChunk chunk = mc.level.getChunk(worldChunkX, worldChunkZ);
                    if (chunk == null) continue;

                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                    for (int x = 0; x < 16; x += 1) {
                        for (int z = 0; z < 16; z += 1) {
                            for (int y = minY; y < maxY; y += 1) {
                                mutablePos.set((worldChunkX << 4) + x, y, (worldChunkZ << 4) + z);

                                Block block = chunk.getBlockState(mutablePos).getBlock();

                                // Check if block is in whitelist
                                if (whitelistedBlocks.contains(block)) {
                                    // Use custom or default color
                                    float[] color = getColorForBlock(block);
                                    oreCache.put(mutablePos.immutable(), color);
                                }
                                // Also check default blocks (for backwards compatibility)
                                else if (DEFAULT_XRAY_BLOCKS.containsKey(block)) {
                                    oreCache.put(mutablePos.immutable(), DEFAULT_XRAY_BLOCKS.get(block));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Skip this chunk if there's an error
                    System.err.println("Error scanning chunk at " + worldChunkX + ", " + worldChunkZ + ": " + e.getMessage());
                }
            }
        }
    }

    public static boolean shouldShowBlock(Block block) {
        return whitelistedBlocks.contains(block) || DEFAULT_XRAY_BLOCKS.containsKey(block);
    }
}