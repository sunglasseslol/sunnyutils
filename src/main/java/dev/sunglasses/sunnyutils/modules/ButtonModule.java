package dev.sunglasses.sunnyutils.modules;

import net.minecraft.client.KeyMapping;

public abstract class ButtonModule extends Module {
    public ButtonModule(String name, int defaultKey, String category) {
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
