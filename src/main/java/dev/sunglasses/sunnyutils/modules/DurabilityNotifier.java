package dev.sunglasses.sunnyutils.modules;

import dev.sunglasses.sunnyutils.SunnyUtils;
import dev.sunglasses.sunnyutils.modules.base.GenericModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DurabilityNotifier extends GenericModule {
    private static final Set<ItemStack> alertedSlots = new HashSet<>();
    public DurabilityNotifier() {
        super("DurabilityNotifier");
    }

    private List<ItemStack> getAllPlayerItems(LocalPlayer player) {
        List<ItemStack> items = new ArrayList<>();
        if (player == null) return items;

        // Main inventory (hotbar + main)
        items.addAll(player.getInventory().getNonEquipmentItems());

        // Armor slots
        items.add(player.getItemBySlot(EquipmentSlot.HEAD));
        items.add(player.getItemBySlot(EquipmentSlot.CHEST));
        items.add(player.getItemBySlot(EquipmentSlot.LEGS));
        items.add(player.getItemBySlot(EquipmentSlot.FEET));

        // Offhand slot
        items.add(player.getItemBySlot(EquipmentSlot.OFFHAND));

        return items;
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if(player == null) return;

        for (ItemStack item : getAllPlayerItems(player)) {
            if(item.isDamageableItem()) {
                // how much durability the item has left
                int healthLeft = item.getMaxDamage() - item.getDamageValue();

                if(healthLeft <= 30 && !alertedSlots.contains(item)) {
                    player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.5F, 1.2F);
                    player.displayClientMessage(Component.literal(item.getStyledHoverName().getString() + " is low durability!"), true);
                    // we add it to the alertedSlots hashset, so we don't spam the sound
                    alertedSlots.add(item);
                } else if (healthLeft > 30) {
                    alertedSlots.remove(item);
                }
            }
        }
    }
}
