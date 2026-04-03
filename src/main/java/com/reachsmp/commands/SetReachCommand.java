package com.reachsmp.commands;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import com.reachsmp.ReachVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetReachCommand implements CommandExecutor, TabCompleter {

    private final ReachSMPPlugin plugin;

    public SetReachCommand(ReachSMPPlugin plugin) {
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
            sender.sendMessage(Component.text("Usage: /setreach <variant>", NamedTextColor.RED));
            return true;
        }

        try {
            ReachVariant variant = ReachVariant.valueOf(args[0].toUpperCase());
            plugin.getAbilityManager().removePermanentEffects(player);
            PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
            data.setVariant(variant);
            data.setVariantLost(false);
            plugin.getAbilityManager().applyPermanentEffects(player);
            player.sendMessage(Component.text("Variant set to " + variant.getDisplayName(), NamedTextColor.GREEN));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid variant. Valid: STRENGTH, RESISTANCE, SPEED, REGENERATION, HEALTH, FIRE_RESISTANCE", NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (ReachVariant v : ReachVariant.values()) {
                if (v.name().startsWith(args[0].toUpperCase())) completions.add(v.name());
            }
        }
        return completions;
    }
}
