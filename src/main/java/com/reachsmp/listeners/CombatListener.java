package com.reachsmp.listeners;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import com.reachsmp.ReachVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class CombatListener implements Listener {

    private final ReachSMPPlugin plugin;

    public CombatListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (!plugin.isSMPStarted()) return;

        plugin.getHitBarManager().addProgress(attacker, event.getDamage());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!plugin.isSMPStarted()) return;

        PlayerData victimData = plugin.getPlayerDataManager().getOrCreate(victim.getUniqueId());
        victimData.addDeath();
        victimData.setVariantLost(true);
        victimData.setHitBar(0.0);
        plugin.getAbilityManager().removePermanentEffects(victim);

        Player killer = victim.getKiller();
        if (killer != null) {
            PlayerData killerData = plugin.getPlayerDataManager().getOrCreate(killer.getUniqueId());
            killerData.addKill();
            if (victimData.canDropReach()) {
                killerData.addReach(plugin.getConfigManager().getReachSteal());
                plugin.getReachManager().applyReach(killer);
                killer.sendMessage(Component.text(
                        "You stole 0.5 reach from " + victim.getName() + "! Reach: " + String.format("%.1f", killerData.getReach()),
                        NamedTextColor.GREEN));
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!plugin.isSMPStarted()) return;
        if (!plugin.getReachManager().isReachBottle(event.getItem())) return;

        event.setCancelled(true);
        // Remove one from hand
        var item = event.getItem().clone();
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        data.addReach(0.5);
        plugin.getReachManager().applyReach(player);
        player.sendMessage(Component.text(
                "Reach increased! Now: " + String.format("%.1f", data.getReach()), NamedTextColor.GREEN));
    }
}
