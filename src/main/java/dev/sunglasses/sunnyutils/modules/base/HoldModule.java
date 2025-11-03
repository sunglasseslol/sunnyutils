package dev.sunglasses.sunnyutils.modules.base;

import net.minecraft.client.KeyMapping;

/**
 * A module that is only active while the key is held down
 */
public abstract class HoldModule extends Module {
    private boolean isHeld = false;
    private boolean wasHeld = false;

    public HoldModule(String name, int defaultKey) {
        super(name);
        setKeyMapping(new KeyMapping(
                "key.sunnyutils.modules." + name.toLowerCase(),
                defaultKey,
                "key.sunnyutils.modules"
        ));
    }

    public boolean isHeld() {
        return isHeld;
    }

    public void onPress() {
        // Override in subclass if needed
    }

    public void onRelease() {
        // Override in subclass if needed
    }

    public void whileHeld() {
        // Override in subclass if needed
    }

    @Override
    public void onTick() {
        KeyMapping km = getKeyMapping();
        if (km == null) return;

        // Check if key is currently held down
        isHeld = km.isDown();

        // Detect press (transition from not held to held)
        if (isHeld && !wasHeld) {
            onPress();
        }
        // Detect release (transition from held to not held)
        else if (!isHeld && wasHeld) {
            onRelease();
        }

        // Call whileHeld every tick the key is down
        if (isHeld) {
            whileHeld();
        }

        wasHeld = isHeld;
    }
}