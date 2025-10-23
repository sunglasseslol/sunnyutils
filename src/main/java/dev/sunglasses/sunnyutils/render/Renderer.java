package dev.sunglasses.sunnyutils.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import dev.sunglasses.sunnyutils.utils.MathUtils;

import java.util.Set;

public class Renderer {
    public static void renderBox(VertexConsumer vc, Matrix4f mat, AABB box, float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;


        // Front face (Z+)
        putQuad(vc, mat, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, r, g, b, a);
        // Back face (Z-)
        putQuad(vc, mat, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, r, g, b, a);
        // Left (X-)
        putQuad(vc, mat, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, r, g, b, a);
        // Right (X+)
        putQuad(vc, mat, x2, y1, z2, x2, y1, z1, x2, y2, z1, x2, y2, z2, r, g, b, a);
        // Top (Y+)
        putQuad(vc, mat, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, r, g, b, a);
        // Bottom (Y-)
        putQuad(vc, mat, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, r, g, b, a);
    }

    private static void putQuad(VertexConsumer vc, Matrix4f mat,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                float r, float g, float b, float a) {
        vc.addVertex(mat, x1, y1, z1).setColor(r, g, b, a);
        vc.addVertex(mat, x2, y2, z2).setColor(r, g, b, a);
        vc.addVertex(mat, x3, y3, z3).setColor(r, g, b, a);
        vc.addVertex(mat, x4, y4, z4).setColor(r, g, b, a);
    }

    public static void renderArea(VertexConsumer consumer, Matrix4f mat, BlockPos first, BlockPos second) {
        int minX = Math.min(first.getX(), second.getX());
        int minY = Math.min(first.getY(), second.getY());
        int minZ = Math.min(first.getZ(), second.getZ());
        int maxX = Math.max(first.getX(), second.getX());
        int maxY = Math.max(first.getY(), second.getY());
        int maxZ = Math.max(first.getZ(), second.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    AABB box = new AABB(pos).inflate(0.001);
                    Renderer.renderBox(consumer, mat, box, 0.2f, 0.7f, 1f, 0.15f);
                }
            }
        }
    }

    public static void renderLine(VertexConsumer consumer, Matrix4f mat, BlockPos first, BlockPos second) {
        Set<BlockPos> lineBlocks = MathUtils.computeLine(first, second);
        for (BlockPos pos : lineBlocks) {
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (lineBlocks.contains(neighbor)) continue;
                AABB box = new AABB(pos).inflate(0.001);
                renderFace(consumer, mat, box, dir, 0.2f, 0.7f, 1f, 0.25f);
            }
        }
    }

    public static void renderFace(VertexConsumer vc, Matrix4f mat, AABB box, Direction dir, float r, float g, float b, float a) {
        double minX = box.minX, minY = box.minY, minZ = box.minZ;
        double maxX = box.maxX, maxY = box.maxY, maxZ = box.maxZ;

        switch (dir) {
            case DOWN -> Renderer.putQuad(vc, mat, (float) minX, (float) minY, (float) minZ, (float) maxX, (float) minY, (float) minZ,
                    (float) maxX, (float) minY, (float) maxZ, (float) minX, (float) minY, (float) maxZ, r, g, b, a);
            case UP -> Renderer.putQuad(vc, mat, (float) minX, (float) maxY, (float) maxZ, (float) maxX, (float) maxY, (float) maxZ,
                    (float) maxX, (float) maxY, (float) minZ, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
            case NORTH -> Renderer.putQuad(vc, mat, (float) maxX, (float) minY, (float) minZ, (float) minX, (float) minY, (float) minZ,
                    (float) minX, (float) maxY, (float) minZ, (float) maxX, (float) maxY, (float) minZ, r, g, b, a);
            case SOUTH -> Renderer.putQuad(vc, mat, (float) minX, (float) minY, (float) maxZ, (float) maxX, (float) minY, (float) maxZ,
                    (float) maxX, (float) maxY, (float) maxZ, (float) minX, (float) maxY, (float) maxZ, r, g, b, a);
            case WEST -> Renderer.putQuad(vc, mat, (float) minX, (float) minY, (float) minZ, (float) minX, (float) minY, (float) maxZ,
                    (float) minX, (float) maxY, (float) maxZ, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
            case EAST -> Renderer.putQuad(vc, mat, (float) maxX, (float) minY, (float) maxZ, (float) maxX, (float) minY, (float) minZ,
                    (float) maxX, (float) maxY, (float) minZ, (float) maxX, (float) maxY, (float) maxZ, r, g, b, a);
        }
    }
}
