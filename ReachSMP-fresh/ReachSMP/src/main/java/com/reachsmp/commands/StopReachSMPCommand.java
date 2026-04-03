package com.reachsmp.commands;

import com.reachsmp.ReachSMPPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StopReachSMPCommand implements CommandExecutor {

    private final ReachSMPPlugin plugin;

    public StopReachSMPCommand(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("reachsmp.admin")) {
            sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
            return true;
        }
        if (!plugin.isSMPStarted()) {
            sender.sendMessage(Component.text("The SMP hasn't started yet!", NamedTextColor.RED));
            return true;
        }

        plugin.setSMPStarted(false);
        plugin.setGracePeriod(false);

        // Hide boss bar if active
        if (plugin.getGraceBossBar() != null) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                p.hideBossBar(plugin.getGraceBossBar());
            }
            plugin.setGraceBossBar(null);
        }

        // Remove all variants and clear effects
        plugin.getPlayerDataManager().clearAllVariants();
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getAbilityManager().removePermanentEffects(p);
            p.sendMessage(Component.text("The ReachSMP has been stopped. All variants removed.", NamedTextColor.RED));
        }

        plugin.getPlayerDataManager().saveAll();
        sender.sendMessage(Component.text("ReachSMP stopped and all variants removed.", NamedTextColor.GREEN));
        return true;
    }
}
