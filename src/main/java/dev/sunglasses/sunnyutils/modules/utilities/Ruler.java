package dev.sunglasses.sunnyutils.modules.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.base.ToggleModule;
import dev.sunglasses.sunnyutils.render.Renderer;
import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.utils.MathUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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

    private static BlockPos firstPos = null;
    private static BlockPos secondPos = null;

    private static boolean areaMode = false;

    public static boolean getAreaMode() { return areaMode; }

    public static void clear() {
        firstPos = null;
        secondPos = null;
    }

    public static void changeAreaMode() { areaMode = !areaMode; }

    public static void setAreaMode(boolean state) {
        areaMode = state;
    }

    private static long lastClickTime = 0;
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
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
        } else if (mc.player.isCrouching() && firstPos != null) {
            secondPos = pos;
            event.getEntity().displayClientMessage(Component.literal("Second point set at " + pos.toShortString()), true);
        }else {
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
        VertexConsumer consumer = bufferSource.getBuffer(Renderer.SOLID_BOX);
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
        bufferSource.endBatch(Renderer.SOLID_BOX);
    }

    @Override
    public void onRender(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        Ruler ruler = ModuleManager.getModule(Ruler.class);
        if (mc.level == null || ruler == null || !ruler.isEnabled()) return;
        if (firstPos == null || secondPos == null) return;

        int count = getBlockCount(firstPos, secondPos, areaMode);
        String text = count + " blocks";

        // Draw near top-left corner of screen
        Gui.drawString(mc, guiGraphics, Component.literal(text), mc.getWindow().getGuiScaledWidth() - mc.font.width(text) - 5, 5, 0xFFFFFFFF);
    }

    public static void renderHighlightBox(VertexConsumer vc, Matrix4f mat, BlockPos pos, float r, float g, float b, float a) {
        AABB box = new AABB(pos).inflate(0.002); // slight inflation to avoid z-fighting
        Renderer.renderBox(vc, mat, box, r, g, b, a);
    }

    private static int getBlockCount(BlockPos first, BlockPos second, boolean areaMode) {
        if (first == null || second == null) return 0;

        if (areaMode) {
            int dx = Math.abs(second.getX() - first.getX()) + 1;
            int dy = Math.abs(second.getY() - first.getY()) + 1;
            int dz = Math.abs(second.getZ() - first.getZ()) + 1;
            return dx * dy * dz; // full volume count
        } else {
            return MathUtils.computeLine(first, second).size(); // all line blocks including endpoints
        }
    }
}
