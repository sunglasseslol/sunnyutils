package dev.sunglasses.sunnyutils.modules.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.render.Renderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = SunnyUtils.MODID)
public class Ruler extends ToggleModule {
    public Ruler() {
        super("Ruler", GLFW.GLFW_KEY_K, "key.sunnyutils.modules");
    }

    private static final RenderType HIGHLIGHT_BOX = RenderType.create(
            "highlight_box",
            1536, // buffer size (same as most built-ins)
            false, // affectsCrumbling
            true,  // sortOnUpload
            RenderPipelines.DEBUG_QUADS, // the pipeline defines POSITION_COLOR
            RenderType.CompositeState.builder()
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(RenderStateShard.NO_OVERLAY)
                    .setLayeringState(RenderStateShard.NO_LAYERING)
                    .setOutputState(RenderStateShard.MAIN_TARGET)
                    .createCompositeState(false)
    );

    private static BlockPos firstPos = null;
    private static BlockPos secondPos = null;

    private static long lastClickTime = 0;
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().level().isClientSide) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return; // <- important

        long time = System.currentTimeMillis();
        if (time - lastClickTime < 200) return; // ignore rapid double events
        lastClickTime = time;

        Ruler ruler = ModuleManager.getModule(Ruler.class);
        if (ruler == null || !ruler.isEnabled()) return;

        BlockPos pos = event.getPos();
        if (firstPos == null) {
            firstPos = pos;
            secondPos = null;
            event.getEntity().displayClientMessage(Component.literal("First point set at " + pos.toShortString()), true);
        } else if (secondPos == null) {
            secondPos = pos;
            event.getEntity().displayClientMessage(Component.literal("Second point set at " + pos.toShortString()), true);
        } else {
            firstPos = pos;
            secondPos = null;
            event.getEntity().displayClientMessage(Component.literal("Reset first point at " + pos.toShortString()), true);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        Ruler ruler = ModuleManager.getModule(Ruler.class);
        if (mc.level == null && !ruler.isEnabled()) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(HIGHLIGHT_BOX);
        Matrix4f mat = poseStack.last().pose();

        // Highlight selected points
        if (firstPos != null) renderHighlightBox(consumer, mat, firstPos, 0f, 1f, 0f, 0.4f);
        if (secondPos != null) renderHighlightBox(consumer, mat, secondPos, 1f, 0f, 0f, 0.4f);

        // If both are set, draw line and highlight blocks between
        if (firstPos != null && secondPos != null) {
            highlightBetweenBlocks(consumer, mat, firstPos, secondPos);
        }

        poseStack.popPose();
        bufferSource.endBatch(HIGHLIGHT_BOX);
    }

    private static void renderHighlightBox(VertexConsumer vc, Matrix4f mat, BlockPos pos, float r, float g, float b, float a) {
        AABB box = new AABB(pos).inflate(0.01);
        Renderer.renderBox(vc, mat, box, r, g, b, a);
    }

    private static void highlightBetweenBlocks(VertexConsumer consumer, Matrix4f mat, BlockPos firstPos, BlockPos secondPos) {
        int minX = Math.min(firstPos.getX(), secondPos.getX());
        int minY = Math.min(firstPos.getY(), secondPos.getY());
        int minZ = Math.min(firstPos.getZ(), secondPos.getZ());
        int maxX = Math.max(firstPos.getX(), secondPos.getX());
        int maxY = Math.max(firstPos.getY(), secondPos.getY());
        int maxZ = Math.max(firstPos.getZ(), secondPos.getZ());

        // Loop through each block
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    AABB box = new AABB(new BlockPos(x, y, z)).inflate(0.01);
                    Renderer.renderBox(consumer, mat, box, 0.2f, 0.7f, 1f, 0.2f); // cyan highlight
                }
            }
        }
    }
}
