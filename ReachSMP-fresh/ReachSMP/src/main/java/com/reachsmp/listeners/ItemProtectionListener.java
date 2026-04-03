package com.reachsmp.listeners;

import com.reachsmp.ReachSMPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ItemProtectionListener implements Listener {

    private final ReachSMPPlugin plugin;

    public ItemProtectionListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    // Placeholder — extend with protection logic as needed
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        // No special protection needed currently
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // No special protection needed currently
    }
}
