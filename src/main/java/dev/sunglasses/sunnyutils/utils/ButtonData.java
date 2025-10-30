package dev.sunglasses.sunnyutils.utils;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Supplier;


public class ButtonData {
    public final String name;
    public final Button.OnPress onPress;

    public ButtonData(String name, Supplier<Screen> screenSupplier, Button.OnPress onPress) {
        this.name = name;
        this.onPress = onPress;
    }
}
