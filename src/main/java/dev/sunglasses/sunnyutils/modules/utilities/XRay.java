package dev.sunglasses.sunnyutils.modules.utilities;

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

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = SunnyUtils.MODID)
public class XRay extends ToggleModule {

    // Map of blocks to their colors (RGBA)
    private static final Map<Block, float[]> XRAY_BLOCKS = new HashMap<>();

    static {
        // Diamonds - Cyan
        XRAY_BLOCKS.put(Blocks.DIAMOND_ORE, new float[]{0.0f, 1.0f, 1.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_DIAMOND_ORE, new float[]{0.0f, 1.0f, 1.0f, 0.8f});

        XRAY_BLOCKS.put(Blocks.COAL_ORE, new float[]{0.0f, 0.0f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_COAL_ORE, new float[]{0.0f, 0.0f, 10.0f, 0.8f});

        // Emeralds - Green
        XRAY_BLOCKS.put(Blocks.EMERALD_ORE, new float[]{0.0f, 1.0f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_EMERALD_ORE, new float[]{0.0f, 1.0f, 0.0f, 0.8f});

        // Gold - Yellow
        XRAY_BLOCKS.put(Blocks.GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.NETHER_GOLD_ORE, new float[]{1.0f, 1.0f, 0.0f, 0.8f});

        // Iron - Light Gray
        XRAY_BLOCKS.put(Blocks.IRON_ORE, new float[]{0.8f, 0.8f, 0.8f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_IRON_ORE, new float[]{0.8f, 0.8f, 0.8f, 0.8f});

        // Lapis - Blue
        XRAY_BLOCKS.put(Blocks.LAPIS_ORE, new float[]{0.2f, 0.2f, 1.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_LAPIS_ORE, new float[]{0.2f, 0.2f, 1.0f, 0.8f});

        // Redstone - Red
        XRAY_BLOCKS.put(Blocks.REDSTONE_ORE, new float[]{1.0f, 0.0f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_REDSTONE_ORE, new float[]{1.0f, 0.0f, 0.0f, 0.8f});

        // Copper - Orange
        XRAY_BLOCKS.put(Blocks.COPPER_ORE, new float[]{1.0f, 0.5f, 0.0f, 0.8f});
        XRAY_BLOCKS.put(Blocks.DEEPSLATE_COPPER_ORE, new float[]{1.0f, 0.5f, 0.0f, 0.8f});

        // Ancient Debris - Purple
        XRAY_BLOCKS.put(Blocks.ANCIENT_DEBRIS, new float[]{0.6f, 0.0f, 0.6f, 0.8f});

        // Nether Quartz - White
        XRAY_BLOCKS.put(Blocks.NETHER_QUARTZ_ORE, new float[]{1.0f, 1.0f, 1.0f, 0.8f});
    }

    // Cache ore positions to avoid rescanning every frame
    private static final Map<BlockPos, float[]> oreCache = new HashMap<>();
    private static int scanTicker = 0;
    private static BlockPos lastPlayerChunk = null;

    public XRay() {
        super("XRay", GLFW.GLFW_KEY_X, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            mc.levelRenderer.allChanged();
        }
        oreCache.clear();
        lastPlayerChunk = null;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        XRay xray = ModuleManager.getModule(XRay.class);
        if (mc.level == null || mc.player == null || xray == null || !xray.isEnabled()) return;

        BlockPos playerPos = mc.player.blockPosition();
        BlockPos playerChunk = new BlockPos(playerPos.getX() >> 4, 0, playerPos.getZ() >> 4);

        // Rescan every 40 ticks (2 seconds) or if player moved to new chunk
        scanTicker++;
        if (scanTicker >= 80 || !playerChunk.equals(lastPlayerChunk)) {
            scanTicker = 0;
            lastPlayerChunk = playerChunk;
            scanForOres(mc, playerPos);
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
        double maxDistSq = 64 * 64; // 64 block radius
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

        int chunkRadius = 4; // Reduced to 3 chunks (7x7 chunk area)
        if (mc.level == null) return;
        int minY = Math.max(mc.level.getMinY(), playerPos.getY() - 64); // Only scan 64 blocks below
        int maxY = Math.min(mc.level.getMaxY(), playerPos.getY() + 64); // Only scan 64 blocks above

        for (int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
            for (int chunkZ = -chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
                int worldChunkX = (playerPos.getX() >> 4) + chunkX;
                int worldChunkZ = (playerPos.getZ() >> 4) + chunkZ;

                LevelChunk chunk = mc.level.getChunk(worldChunkX, worldChunkZ);

                // Scan blocks in chunk - sample every 2nd block for speed
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int x = 0; x < 16; x += 1) { // Can increase to x += 2 for even more performance
                    for (int z = 0; z < 16; z += 1) {
                        for (int y = minY; y < maxY; y += 1) { // Can increase to y += 2 for more performance
                            mutablePos.set((worldChunkX << 4) + x, y, (worldChunkZ << 4) + z);

                            Block block = chunk.getBlockState(mutablePos).getBlock();
                            float[] color = XRAY_BLOCKS.get(block);

                            if (color != null) {
                                oreCache.put(mutablePos.immutable(), color);
                            }
                        }
                    }
                }
            }
        }
    }
}