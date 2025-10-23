package dev.sunglasses.sunnyutils;

import dev.sunglasses.sunnyutils.render.gui.Gui;
import dev.sunglasses.sunnyutils.modules.base.*;
import dev.sunglasses.sunnyutils.modules.base.Module;
import dev.sunglasses.sunnyutils.modules.hud.*;
import dev.sunglasses.sunnyutils.modules.screen.*;
import dev.sunglasses.sunnyutils.modules.utilities.*;
import dev.sunglasses.sunnyutils.utils.KeyMappingManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
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

    public SunnyUtils(IEventBus modEventBus, ModContainer modContainer) {
        // register modules
        new Fullbright();
        new ClearChat();
        new WorldData();
        new Hitboxes();
        new ChunkBorders();
        new DurabilityNotifier();
        new ConfigScreenOpener();
        new UtilityScreenOpener();
        new Ruler();

        // register the key bindings
        modEventBus.addListener(KeyMappingManager::registerAllKeyMappings);
        modContainer.registerConfig(ModConfig.Type.COMMON, dev.sunglasses.sunnyutils.utils.Options.SPEC);

        // listens to anything in this class with @SubscribeEvent annotation
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        for (Module module : ModuleManager.getModules()) {
            KeyMapping km = module.getKeyMapping();
            // event.getAction == 1 <- we do this because if you don't, the module gets spam toggled.
            // action 1 is equal to GLFW_PRESS which means it only fires on PRESS, it doesn't repeat, nor does it fire on releasing the key.
            if (km != null && km.consumeClick() && event.getAction() == 1) {
                module.toggle();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGuiEvent.Post event) {
        // sorta useless SOB
        //Gui.drawString(Minecraft.getInstance(), event.getGuiGraphics(), "sunnyutils v" + MOD_VERSION, 5, Minecraft.getInstance().getWindow().getGuiScaledHeight() - 10, 0xFFFFFFFF);
        for (Module module : ModuleManager.getModules()) {
            module.onRender(event.getGuiGraphics());
        }
        //Render toggled modules
        Gui.drawModules(Minecraft.getInstance(), event.getGuiGraphics(), ModuleManager.getModules());
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Post event) {
        for (Module module : ModuleManager.getModules()) {
            module.onTick();
        }
    }
}
