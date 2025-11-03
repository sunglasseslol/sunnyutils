package dev.sunglasses.sunnyutils;

import com.mojang.logging.LogUtils;
import dev.sunglasses.sunnyutils.modules.base.Module;
import dev.sunglasses.sunnyutils.modules.base.ModuleManager;
import dev.sunglasses.sunnyutils.modules.hud.*;
import dev.sunglasses.sunnyutils.modules.screen.*;
import dev.sunglasses.sunnyutils.modules.utilities.*;
import dev.sunglasses.sunnyutils.modules.utilities.submodules.*;
import dev.sunglasses.sunnyutils.modules.world.*;
import dev.sunglasses.sunnyutils.render.gui.*;
import dev.sunglasses.sunnyutils.utils.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(SunnyUtils.MODID)
public class SunnyUtils {
    public static final String MODID = "sunnyutils";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SunnyUtils(IEventBus modEventBus) {
        // register modules
        new Fullbright();
        new ClearChat();
        new WorldData();
        new Hitboxes();
        new ChunkBorders();
        new DurabilityNotifier();
        new ConfigScreenOpener();
        new Ruler();
        new Scaffold();
        new XRay();
        new ShowChat();
        new Radar();

        // register submodules
        new RulerAreaToggle();
        new RulerClear();

        // register the key bindings
        modEventBus.addListener(KeyMappingManager::registerAllKeyMappings);

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
        RainbowText.tick();

        for (Module module : ModuleManager.getModules()) {
            module.onTick();
        }
    }
}
