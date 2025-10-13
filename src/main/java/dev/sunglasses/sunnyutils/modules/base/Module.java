package dev.sunglasses.sunnyutils.modules.base;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Module {
    private final String name;
    private KeyMapping keyMapping;

    public Module(String name) {
        this.name = name;
        ModuleManager.register(this); // auto-register
    }

    public String getName() { return name; }

    public void setKeyMapping(KeyMapping km) { this.keyMapping = km; }
    public KeyMapping getKeyMapping() { return keyMapping; }

    public void toggle() {}
    public void onRender(GuiGraphics guiGraphics) {}
    public void onTick() {}
}
