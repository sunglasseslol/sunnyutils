package dev.sunglasses.sunnyutils.modules.utilities;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class XRay extends ToggleModule {

    // Blocks that will be visible in XRay mode
    private static final Set<Block> XRAY_BLOCKS = new HashSet<>();

    static {
        // Ores
        XRAY_BLOCKS.add(Blocks.COAL_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        XRAY_BLOCKS.add(Blocks.IRON_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        XRAY_BLOCKS.add(Blocks.GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.DIAMOND_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        XRAY_BLOCKS.add(Blocks.EMERALD_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        XRAY_BLOCKS.add(Blocks.LAPIS_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        XRAY_BLOCKS.add(Blocks.REDSTONE_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        XRAY_BLOCKS.add(Blocks.COPPER_ORE);
        XRAY_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        XRAY_BLOCKS.add(Blocks.NETHER_GOLD_ORE);
        XRAY_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        XRAY_BLOCKS.add(Blocks.ANCIENT_DEBRIS);

        // Valuable blocks
        XRAY_BLOCKS.add(Blocks.DIAMOND_BLOCK);
        XRAY_BLOCKS.add(Blocks.EMERALD_BLOCK);
        XRAY_BLOCKS.add(Blocks.GOLD_BLOCK);
        XRAY_BLOCKS.add(Blocks.IRON_BLOCK);
        XRAY_BLOCKS.add(Blocks.NETHERITE_BLOCK);

        // Utility blocks
        XRAY_BLOCKS.add(Blocks.CHEST);
        XRAY_BLOCKS.add(Blocks.TRAPPED_CHEST);
        XRAY_BLOCKS.add(Blocks.ENDER_CHEST);
        XRAY_BLOCKS.add(Blocks.BARREL);
        XRAY_BLOCKS.add(Blocks.SPAWNER);
        XRAY_BLOCKS.add(Blocks.END_PORTAL_FRAME);

        // Nether
        XRAY_BLOCKS.add(Blocks.NETHER_BRICKS);
        XRAY_BLOCKS.add(Blocks.NETHER_BRICK_STAIRS);
        XRAY_BLOCKS.add(Blocks.NETHER_BRICK_FENCE);

        // Useful
        XRAY_BLOCKS.add(Blocks.WATER);
        XRAY_BLOCKS.add(Blocks.LAVA);
        XRAY_BLOCKS.add(Blocks.OBSIDIAN);
        XRAY_BLOCKS.add(Blocks.CRYING_OBSIDIAN);
        XRAY_BLOCKS.add(Blocks.BEDROCK);
    }

    public XRay() {
        super("XRay", GLFW.GLFW_KEY_X, "key.sunnyutils.modules");
    }

    @Override
    public void onToggle() {
        Minecraft mc = Minecraft.getInstance();

        // Force chunk rebuild when toggling
        if (mc.level != null) {
            mc.levelRenderer.allChanged();
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        // bleh new logic
    }

    /**
     * Check if a block should be visible in XRay mode
     */
    public static boolean shouldShowBlock(Block block) {
        return XRAY_BLOCKS.contains(block);
    }
}