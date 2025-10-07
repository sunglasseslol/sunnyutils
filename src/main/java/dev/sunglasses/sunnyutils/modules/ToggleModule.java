package dev.sunglasses.sunnyutils.modules;

import net.minecraft.client.KeyMapping;

public abstract class ToggleModule extends Module {
    private boolean enabled = false;

    public ToggleModule(String name, int defaultKey, String category) {
        super(name);
        setKeyMapping(new KeyMapping(
                "key.sunnyutils.modules." + name.toLowerCase(),
                defaultKey,
                category
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
