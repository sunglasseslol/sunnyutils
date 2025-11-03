package dev.sunglasses.sunnyutils.render.gui.screens.handlers;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.render.gui.screens.CustomPause;
import net.minecraft.client.gui.screens.PauseScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = SunnyUtils.MODID, value = Dist.CLIENT)
public class PauseScreenHandler {
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof PauseScreen) {
            event.setNewScreen(new CustomPause());
        }
    }
}
