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

                    double percentage = data.getHitBar() / hitBarMax;
                    int filled = (int) (percentage * barLength);
                    int empty = barLength - filled;

                    StringBuilder bar = new StringBuilder();
                    String abilityLabel;
                    if (data.isVariantLost() || data.getVariant() == null) {
                        abilityLabel = "No Ability";
                    } else {
                        abilityLabel = "Ability " + String.format("%.0f", data.getHitBar()) + "%";
                    }
                    bar.append(abilityLabel).append(" ");
                    bar.append("|".repeat(filled));
                    bar.append(" ".repeat(empty));

                    NamedTextColor color = data.isVariantLost() ? NamedTextColor.RED :
                            (data.isHitBarFull() ? NamedTextColor.GREEN : NamedTextColor.YELLOW);

                    player.sendActionBar(Component.text(bar.toString(), color, TextDecoration.BOLD));
                }
            }
        }.runTaskTimer(plugin, 0L, plugin.getConfigManager().getBarDisplayInterval());
    }

    public void addProgress(Player attacker, double damage) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(attacker.getUniqueId());
        if (data.isVariantLost()) return;

        ConfigManager config = plugin.getConfigManager();
        double gain = damage * config.getDamageMultiplier();
        data.addHitBar(gain);

        if (data.isHitBarFull()) {
            data.setHitBar(0.0);
            plugin.getAbilityManager().activateAbility(attacker, data);
        }
    }
}
