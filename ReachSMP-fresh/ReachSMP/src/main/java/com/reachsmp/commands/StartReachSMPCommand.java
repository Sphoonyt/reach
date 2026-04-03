package com.reachsmp.commands;

import com.reachsmp.ReachSMPPlugin;
import com.reachsmp.ReachVariant;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class StartReachSMPCommand implements CommandExecutor {

    private final ReachSMPPlugin plugin;

    public StartReachSMPCommand(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("reachsmp.admin")) {
            sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
            return true;
        }
        if (plugin.isSMPStarted()) {
            sender.sendMessage(Component.text("The SMP has already started!", NamedTextColor.RED));
            return true;
        }

        int countdownSeconds = plugin.getConfigManager().getCountdownSeconds();
        sender.sendMessage(Component.text("Starting ReachSMP in " + countdownSeconds + " seconds...", NamedTextColor.YELLOW));

        new BukkitRunnable() {
            int remaining = countdownSeconds;

            @Override
            public void run() {
                if (remaining > 0) {
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        p.sendMessage(Component.text("ReachSMP starting in " + remaining + "...", NamedTextColor.YELLOW));
                    }
                    remaining--;
                } else {
                    cancel();
                    launchSMP();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        return true;
    }

    private void launchSMP() {
        plugin.setSMPStarted(true);

        // Assign variants to all online players
        plugin.getPlayerDataManager().assignRandomVariantsToAll();

        // Set world border
        var world = plugin.getServer().getWorlds().get(0);
        int borderSize = plugin.getConfigManager().getWorldBorderSize();
        world.getWorldBorder().setSize(borderSize);

        // Start grace period
        plugin.setGracePeriod(true);
        long graceTicks = plugin.getConfigManager().getGraceDurationMinutes() * 60L * 20L;

        BossBar bossBar = BossBar.bossBar(
                Component.text("Grace Period", NamedTextColor.GREEN),
                1.0f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        plugin.setGraceBossBar(bossBar);

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.showBossBar(bossBar);
            var data = plugin.getPlayerDataManager().getOrCreate(p.getUniqueId());
            plugin.getReachManager().applyReach(p);
            plugin.getAbilityManager().applyPermanentEffects(p);
            p.sendMessage(Component.text(
                    "ReachSMP has started! Your ability: "
                            + (data.getVariant() != null ? data.getVariant().getDisplayName() : "None"),
                    NamedTextColor.GREEN));
        }

        // Shrink border after grace period
        new BukkitRunnable() {
            long ticksLeft = graceTicks;

            @Override
            public void run() {
                if (ticksLeft <= 0) {
                    cancel();
                    plugin.setGracePeriod(false);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        p.hideBossBar(bossBar);
                        p.sendMessage(Component.text("Grace period has ended! PvP is now enabled!", NamedTextColor.RED));
                    }
                    world.getWorldBorder().setSize(
                            plugin.getConfigManager().getWorldBorderSize() / 2.0,
                            plugin.getConfigManager().getWorldBorderExpandSeconds());
                    return;
                }
                float progress = (float) ticksLeft / graceTicks;
                bossBar.progress(Math.max(0f, Math.min(1f, progress)));
                ticksLeft -= 20;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
