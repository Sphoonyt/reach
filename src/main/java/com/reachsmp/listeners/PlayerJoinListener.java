package com.reachsmp.listeners;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import com.reachsmp.ReachVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    private final ReachSMPPlugin plugin;

    public PlayerJoinListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        data.setJoinTimestamp(System.currentTimeMillis());

        if (!plugin.isSMPStarted()) return;

        // Assign variant if needed
        if (data.getVariant() == null && !data.isVariantLost()) {
            data.setVariant(ReachVariant.random());
            player.sendMessage(Component.text(
                    "You have been assigned the " + data.getVariant().getDisplayName() + " ability!",
                    NamedTextColor.GREEN));
        }

        // Apply reach and permanent effects
        plugin.getReachManager().applyReach(player);
        if (!data.isVariantLost()) {
            plugin.getAbilityManager().applyPermanentEffects(player);
        }

        // Inform player of current status
        if (data.isVariantLost()) {
            player.sendMessage(Component.text("You have lost your ability.", NamedTextColor.RED));
        } else if (data.getVariant() != null) {
            player.sendMessage(Component.text(
                    "Your ability: " + data.getVariant().getDisplayName()
                            + " | Reach: " + String.format("%.1f", data.getReach()),
                    NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        long sessionTime = System.currentTimeMillis() - data.getJoinTimestamp();
        data.addPlayTime(sessionTime);
    }
}
