package com.reachsmp.commands;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetReachLevelCommand implements CommandExecutor, TabCompleter {

    private final ReachSMPPlugin plugin;

    public SetReachLevelCommand(ReachSMPPlugin plugin) {
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
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /setreachlevel <value>", NamedTextColor.RED));
            return true;
        }
        try {
            double value = Double.parseDouble(args[0]);
            PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
            data.setReach(value);
            plugin.getReachManager().applyReach(player);
            player.sendMessage(Component.text("Reach set to " + String.format("%.1f", data.getReach()), NamedTextColor.GREEN));
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number: " + args[0], NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        return List.of("3.0", "4.0", "5.0", "6.0");
    }
}
