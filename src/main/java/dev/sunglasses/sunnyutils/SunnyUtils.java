package dev.sunglasses.sunnyutils;

import dev.sunglasses.sunnyutils.modules.*;
import dev.sunglasses.sunnyutils.modules.Module;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.fml.common.Mod;
import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SunnyUtils.MODID)
public class SunnyUtils {
    public static final String MODID = "sunnyutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SunnyUtils(IEventBus modEventBus) {
        new Fullbright();
        new ClearChat();
        new Coordinates();

        modEventBus.addListener(KeyMappingManager::registerAllKeyMappings);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        for (Module module : ModuleManager.getModules()) {
            KeyMapping km = module.getKeyMapping();
            if (km != null && km.consumeClick()) {
                module.toggle();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGuiEvent.Post event) {
        for (Module module : ModuleManager.getModules()) {
            module.onRender(event.getGuiGraphics());
        }
    }
}
