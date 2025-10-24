package dev.sunglasses.sunnyutils.utils;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class MathUtils {
    public static Set<BlockPos> computeLine(BlockPos start, BlockPos end) {
        Set<BlockPos> result = new HashSet<>();

        int x1 = start.getX(), y1 = start.getY(), z1 = start.getZ();
        int x2 = end.getX(), y2 = end.getY(), z2 = end.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int xs = x1 < x2 ? 1 : -1;
        int ys = y1 < y2 ? 1 : -1;
        int zs = z1 < z2 ? 1 : -1;

        int p1, p2;

        // Driving axis X
        if (dx >= dy && dx >= dz) {
            p1 = 2 * dy - dx;
            p2 = 2 * dz - dx;
            while (x1 != x2) {
                result.add(new BlockPos(x1, y1, z1));
                x1 += xs;
                if (p1 >= 0) { y1 += ys; p1 -= 2 * dx; }
                if (p2 >= 0) { z1 += zs; p2 -= 2 * dx; }
                p1 += 2 * dy;
                p2 += 2 * dz;
            }
        }
        // Driving axis Y
        else if (dy >= dx && dy >= dz) {
            p1 = 2 * dx - dy;
            p2 = 2 * dz - dy;
            while (y1 != y2) {
                result.add(new BlockPos(x1, y1, z1));
                y1 += ys;
                if (p1 >= 0) { x1 += xs; p1 -= 2 * dy; }
                if (p2 >= 0) { z1 += zs; p2 -= 2 * dy; }
                p1 += 2 * dx;
                p2 += 2 * dz;
            }
        }
        // Driving axis Z
        else {
            p1 = 2 * dy - dz;
            p2 = 2 * dx - dz;
            while (z1 != z2) {
                result.add(new BlockPos(x1, y1, z1));
                z1 += zs;
                if (p1 >= 0) { y1 += ys; p1 -= 2 * dz; }
                if (p2 >= 0) { x1 += xs; p2 -= 2 * dz; }
                p1 += 2 * dy;
                p2 += 2 * dx;
            }
        }

        result.add(end);
        return result;
    }
}
