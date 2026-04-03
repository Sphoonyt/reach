package com.reachsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HitBarManager {

    private final ReachSMPPlugin plugin;

    public HitBarManager(ReachSMPPlugin plugin) {
        this.plugin = plugin;
        startDisplayTask();
    }

    private void startDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                ConfigManager config = plugin.getConfigManager();
                int barLength = config.getBarDisplayLength();
                double hitBarMax = config.getHitBarMax();

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());

                    String abilityLabel;
                    NamedTextColor color;

                    if (data.isVariantLost() || data.getVariant() == null) {
                        abilityLabel = "No Ability";
                        color = NamedTextColor.RED;
                    } else {
                        double pct = data.getHitBar();
                        abilityLabel = data.getVariant().getDisplayName() + " " + String.format("%.0f", pct) + "%";
                        color = data.isHitBarFull() ? NamedTextColor.GREEN : NamedTextColor.YELLOW;
                    }

                    double percentage = data.getHitBar() / hitBarMax;
                    int filled = (int) (percentage * barLength);
                    int empty = barLength - filled;

                    String bar = abilityLabel + " " + "|".repeat(filled) + " ".repeat(empty);

                    player.sendActionBar(Component.text(bar, color, TextDecoration.BOLD));
                }
            }
        }.runTaskTimer(plugin, 0L, plugin.getConfigManager().getBarDisplayInterval());
    }

    // Called when a player hits someone — adds 2% per hit, activates ability if bar reaches 100%
    public void onHit(Player attacker) {
        if (!plugin.isSMPStarted()) return;

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(attacker.getUniqueId());
        if (data.isVariantLost() || data.getVariant() == null) return;

        data.addHitBar(2.0); // +2% per hit

        if (data.isHitBarFull()) {
            data.setHitBar(0.0);
            plugin.getAbilityManager().activateAbility(attacker, data);
        }
    }
}
