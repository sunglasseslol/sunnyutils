package dev.sunglasses.sunnyutils.utils;

public class RainbowText {

    private static int tickCounter = 0;

    /**
     * Call this every tick to update the rainbow animation
     */
    public static void tick() {
        tickCounter++;
    }

    /**
     * Get a rainbow color that animates over time
     * @return RGB color as integer (0xRRGGBB format)
     */
    public static int getRainbowColor() {
        float hue = (tickCounter * 0.0025f) % 1.0f;
        return hueToRgb(hue);
    }

    /**
     * Get a rainbow color with speed and offset
     * @param speed How fast the colors change
     * @param offset Starting offset in the rainbow cycle
     * @return RGB color as integer
     */
    public static int getRainbowColor(float speed, float offset) {
        float hue = (tickCounter * speed + offset) % 1.0f;
        return hueToRgb(hue);
    }

    /**
     * Convert HSV hue (0-1) to RGB color integer
     */
    private static int hueToRgb(float hue) {
        float h = hue * 6.0f;
        int i = (int) Math.floor(h);
        float f = h - i;

        int r = 0, g = 0, b = 0;

        switch (i % 6) {
            case 0: r = 255; g = (int)(f * 255); b = 0; break;
            case 1: r = (int)((1 - f) * 255); g = 255; b = 0; break;
            case 2: r = 0; g = 255; b = (int)(f * 255); break;
            case 3: r = 0; g = (int)((1 - f) * 255); b = 255; break;
            case 4: r = (int)(f * 255); g = 0; b = 255; break;
            case 5: r = 255; g = 0; b = (int)((1 - f) * 255); break;
        }

        // Add alpha channel (0xFF = fully opaque)
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /**
     * Reset the tick counter
     */
    public static void reset() {
        tickCounter = 0;
    }
}