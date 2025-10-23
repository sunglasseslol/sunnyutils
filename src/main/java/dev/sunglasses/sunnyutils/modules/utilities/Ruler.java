package dev.sunglasses.sunnyutils.modules.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.render.Renderer;
import dev.sunglasses.sunnyutils.utils.MathUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.Set;

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

    private static boolean areaMode = false;

    public void changeAreaMode() {
        areaMode = !areaMode;
    }

    public void setAreaMode(boolean state) {
        areaMode = state;
    }

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
        if (mc.level == null || ruler == null || !ruler.isEnabled()) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(HIGHLIGHT_BOX);
        Matrix4f mat = poseStack.last().pose();

        // Green = first point, Red = second point
        if (firstPos != null) renderHighlightBox(consumer, mat, firstPos, 0f, 1f, 0f, 0.4f);
        if (secondPos != null) renderHighlightBox(consumer, mat, secondPos, 1f, 0f, 0f, 0.4f);

        if (firstPos != null && secondPos != null) {
            if (areaMode) {
                Renderer.renderArea(consumer, mat, firstPos, secondPos);
            } else {
                Renderer.renderLine(consumer, mat, firstPos, secondPos);
            }
        }

        poseStack.popPose();
        bufferSource.endBatch(HIGHLIGHT_BOX);
    }

    private static void renderHighlightBox(VertexConsumer vc, Matrix4f mat, BlockPos pos, float r, float g, float b, float a) {
        AABB box = new AABB(pos).inflate(0.002); // slight inflation to avoid z-fighting
        Renderer.renderBox(vc, mat, box, r, g, b, a);
    }

    private static void highlightBetweenBlocks(VertexConsumer consumer, Matrix4f mat, BlockPos firstPos, BlockPos secondPos) {
        Set<BlockPos> lineBlocks = MathUtils.computeLine(firstPos, secondPos);

        for (BlockPos pos : lineBlocks) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (lineBlocks.contains(neighbor)) continue; // skip internal face

                AABB box = new AABB(pos).inflate(0.001); // small inflation to prevent flicker
                Renderer.renderFace(consumer, mat, box, dir, 0.2f, 0.7f, 1f, 0.25f); // cyan faces
            }
        }
    }
}
