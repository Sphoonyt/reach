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

public class InfoCommand implements CommandExecutor {

    private final ReachSMPPlugin plugin;

    public InfoCommand(ReachSMPPlugin plugin) {
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
        String variant = data.getVariant() != null ? data.getVariant().getDisplayName() : "None";
        String status = data.isVariantLost() ? " (LOST)" : "";

        player.sendMessage(Component.text("=== ReachSMP Stats ===", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Ability: " + variant + status, NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Reach: " + String.format("%.1f", data.getReach()), NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Ability Bar: " + String.format("%.0f", data.getHitBar()) + "%", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Kills: " + data.getKills() + " | Deaths: " + data.getDeaths(), NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Play Time: " + String.format("%.2f", data.getPlayTimeDays()) + " days", NamedTextColor.YELLOW));
        return true;
    }
}
