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

public class WithdrawCommand implements CommandExecutor {

    private final ReachSMPPlugin plugin;

    public WithdrawCommand(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        if (!data.canDropReach()) {
            player.sendMessage(Component.text("You don't have enough reach to withdraw!", NamedTextColor.RED));
            return true;
        }
        data.removeReach(0.5);
        plugin.getReachManager().applyReach(player);
        var bottle = plugin.getReachManager().createReachBottle();
        player.getInventory().addItem(bottle);
        player.sendMessage(Component.text(
                "Withdrew 0.5 reach. Reach now: " + String.format("%.1f", data.getReach()),
                NamedTextColor.GREEN));
        return true;
    }
}
