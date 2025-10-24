package dev.sunglasses.sunnyutils.modules.base;

import net.minecraft.client.KeyMapping;

public abstract class Submodule extends Module {
    private boolean enabled = false;
    private ToggleModule parentModule;

    public Submodule(String name, int defaultKey, String category, ToggleModule parentModule) {
        super(name);
        this.parentModule = parentModule;
        setKeyMapping(new KeyMapping(
                "key.sunnyutils.modules." + name.toLowerCase(),
                defaultKey,
                category
        ));
    }

    public boolean isEnabled() { return enabled; }

    public boolean isParentEnabled() { return parentModule.isEnabled(); }

    @Override
    public void toggle() {
        onPress();
    }

    public void onPress() { }
}