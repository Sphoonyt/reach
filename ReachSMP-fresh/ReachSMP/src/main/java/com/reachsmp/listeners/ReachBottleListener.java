package com.reachsmp.listeners;

import com.reachsmp.ReachSMPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ReachBottleListener implements Listener {

    private final ReachSMPPlugin plugin;

    public ReachBottleListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        var item = event.getItemDrop().getItemStack();
        // Prevent dropping reroll items on the ground to protect them
        if (plugin.getReachManager().isRerollItem(item)) {
            // Allow dropping — just intercept if you want protection
            // Currently left as passthrough (vanilla behaviour)
        }
    }
}
