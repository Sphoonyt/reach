package com.reachsmp.listeners;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ReachBottleListener implements Listener {

    private final ReachSMPPlugin plugin;

    public ReachBottleListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only fire once (main hand)
        if (event.getHand() != EquipmentSlot.HAND) return;

        // Only right-click
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!plugin.getReachManager().isReachBottle(item)) return;

        // Cancel so the bottle doesn't get thrown
        event.setCancelled(true);

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        data.addReach(0.5);
        plugin.getReachManager().applyReach(player);

        // Consume one bottle
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);

        player.sendMessage(Component.text(
                "Reach increased! Now: " + String.format("%.1f", data.getReach()), NamedTextColor.GREEN));
    }
}
