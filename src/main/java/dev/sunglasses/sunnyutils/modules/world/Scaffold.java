package dev.sunglasses.sunnyutils.modules.world;

import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

public class Scaffold extends ToggleModule {

    public Scaffold() {
        super("Scaffold", GLFW.GLFW_KEY_V, "key.sunnyutils.modules");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) return;

        // Check if player is moving
        if (!isPlayerMoving(player)) return;

        // Get the position below the player
        BlockPos belowPos = player.blockPosition().below();

        // Check if there's air below the player
        if (!level.getBlockState(belowPos).isAir()) return;

        // Find a block item in inventory
        ItemStack blockStack = findBlockInInventory(player);
        if (blockStack.isEmpty()) return;

        // Switch to the block (if not already holding it)
        int blockSlot = findBlockSlotInHotbar(player);
        if (blockSlot == -1) return;

        player.getInventory().setSelectedSlot(blockSlot);

        // Try to place the block
        placeBlock(player, level, belowPos);
    }

    private boolean isPlayerMoving(LocalPlayer player) {
        Vec3 velocity = player.getDeltaMovement();
        return Math.abs(velocity.x) > 0.01 || Math.abs(velocity.z) > 0.01;
    }

    private ItemStack findBlockInInventory(LocalPlayer player) {
        // Check hotbar first
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem && !stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private int findBlockSlotInHotbar(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem && !stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private void placeBlock(LocalPlayer player, Level level, BlockPos pos) {
        // Find a neighboring block to place against
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState neighborState = level.getBlockState(neighbor);

            // Check if this neighbor is a solid block we can place against
            if (!neighborState.isAir() && neighborState.isSolid()) {
                Direction opposite = dir.getOpposite();

                // Calculate hit position on the neighbor block
                Vec3 hitVec = Vec3.atCenterOf(neighbor).add(
                        opposite.getStepX() * 0.5,
                        opposite.getStepY() * 0.5,
                        opposite.getStepZ() * 0.5
                );

                BlockHitResult hitResult = new BlockHitResult(
                        hitVec,
                        opposite,
                        neighbor,
                        false
                );

                // Place the block
                Minecraft mc = Minecraft.getInstance();
                if (mc.gameMode != null) {
                    mc.gameMode.useItemOn(
                            player,
                            InteractionHand.MAIN_HAND,
                            hitResult
                    );
                }

                return; // Successfully placed, exit
            }
        }
    }
}