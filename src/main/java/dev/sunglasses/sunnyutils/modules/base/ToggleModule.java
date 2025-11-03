package dev.sunglasses.sunnyutils.modules.base;

import net.minecraft.client.KeyMapping;

public abstract class ToggleModule extends Module {
    private boolean enabled = false;

    public ToggleModule(String name, int defaultKey) {
        super(name);
        setKeyMapping(new KeyMapping(
                "key.sunnyutils.modules." + name.toLowerCase(),
                defaultKey,
                "key.sunnyutils.modules"
        ));
    }

    public boolean isEnabled() { return enabled; }

    @Override
    public void toggle() {
        enabled = !enabled;
        onToggle();
    }

    public void onToggle() {}
}
