package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.modules.base.GenericModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DurabilityNotifier extends GenericModule {
    private static final Set<Integer> alertedSlots = new HashSet<>();
    public DurabilityNotifier() {
        super("DurabilityNotifier");
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if(player == null) return;

        for (int i = 0; i < player.getInventory().getNonEquipmentItems().size(); i++) {
            ItemStack stack = player.getInventory().getNonEquipmentItems().get(i);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int remaining = stack.getMaxDamage() - stack.getDamageValue();
                if (remaining <= 30 && !alertedSlots.contains(i)) {
                    player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 1.2F);
                    player.displayClientMessage(Component.literal(stack.getDisplayName().getString() + " is low durability!"), true);
                    alertedSlots.add(i);
                } else if (remaining > 30) {
                    alertedSlots.remove(i);
                }
            }
        }
    }
}
