package dev.sunglasses.sunnyutils.utils;

import dev.sunglasses.sunnyutils.modules.base.Module;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class KeyMappingManager {

    public static void registerAllKeyMappings(RegisterKeyMappingsEvent event) {
        for (Module module : ModuleManager.getModules()) {
            KeyMapping km = module.getKeyMapping();
            if (km != null) {
                event.register(km);
            }
        }
    }
}
