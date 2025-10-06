package dev.sunglasses.sunnyutils;

import dev.sunglasses.sunnyutils.modules.*;
import dev.sunglasses.sunnyutils.modules.Module;
import dev.sunglasses.sunnyutils.gui.Gui;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
    public static final String MOD_VERSION = "1.0.0";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SunnyUtils(IEventBus modEventBus) {
        new Fullbright();
        new ClearChat();
        new Coordinates();
        new Hitboxes();
        new ChunkBorders();

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
        Gui.drawString(Minecraft.getInstance(), event.getGuiGraphics(), "sunnyutils v" + MOD_VERSION, 5, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 10, 0xFFFFFFFF);
        for (Module module : ModuleManager.getModules()) {
            module.onRender(event.getGuiGraphics());
        }
        //Render toggled modules
        Gui.drawModules(Minecraft.getInstance(), event.getGuiGraphics(), ModuleManager.getModules());
    }
}
