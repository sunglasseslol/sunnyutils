package dev.sunglasses.sunnyutils.modules.utilities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

public class Ruler {
    public Ruler() {}

    public int distance(int block1, int block2) {
        return block1 - block2;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult == null || mc.level == null) return;
        if (mc.hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult blockHit = (BlockHitResult) mc.hitResult;
        BlockPos pos = blockHit.getBlockPos();

        PoseStack poseStack = event.getPoseStack();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

//        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.translucent());
        AABB box = new AABB(pos).inflate(0.001); // slightly bigger than block to avoid z-fighting

        float red = 0.2f;
        float green = 0.8f;
        float blue = 1.0f;
        float alpha = 0.4f; // adjust for opacity

        renderSolidBox(poseStack, consumer, box, red, green, blue, alpha);

        poseStack.popPose();
    }

    private static void renderSolidBox(PoseStack poseStack, VertexConsumer consumer, AABB box, float r, float g, float b, float a) {
        Matrix4f matrix = poseStack.last().pose();

        // Draw each face as two triangles — simplified version
//        LevelRenderer.renderLineBox(poseStack, consumer, box, r, g, b, a);
    }
}
