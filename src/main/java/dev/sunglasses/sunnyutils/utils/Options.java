package dev.sunglasses.sunnyutils.utils;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Options {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue AREA_MODE = BUILDER
            .comment("Whether or not the ruler should show a direct line between the two blocks, or create an area outline.")
            .define("areaMode", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
