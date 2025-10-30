package dev.sunglasses.sunnyutils.utils;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;

public class ScreenButtonData {
    public final String name;
    public final Supplier<Screen> screenSupplier;

    public ScreenButtonData(String name, Supplier<Screen> screenSupplier) {
        this.name = name;
        this.screenSupplier = screenSupplier;
    }
}
