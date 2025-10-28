package dev.sunglasses.sunnyutils.modules.base;

import net.minecraft.client.KeyMapping;

public abstract class ButtonModule extends Module {
    public ButtonModule(String name, int defaultKey, KeyMapping.Category category) {
        super(name);
        setKeyMapping(new KeyMapping(
                "key.sunnyutils.modules." + name.toLowerCase(),
                defaultKey,
                category
        ));
    }

    @Override
    public void toggle() {
        onPress();
    }

    public void onPress() {

    }
}
