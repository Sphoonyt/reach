package com.reachsmp.commands;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FillBarCommand implements CommandExecutor {

    private final ReachSMPPlugin plugin;

    public FillBarCommand(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("reachsmp.admin")) {
            sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        data.setHitBar(plugin.getConfigManager().getHitBarMax());
        player.sendMessage(Component.text("Ability bar filled to 100%!", NamedTextColor.GREEN));
        return true;
    }
}
