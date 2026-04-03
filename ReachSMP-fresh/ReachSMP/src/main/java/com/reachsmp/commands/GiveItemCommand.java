package com.reachsmp.commands;

import com.reachsmp.ReachSMPPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveItemCommand implements CommandExecutor, TabCompleter {

    private final ReachSMPPlugin plugin;

    public GiveItemCommand(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("reachsmp.admin")) {
            sender.sendMessage(Component.text("You don't have permission!", NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /giveitem <item> [amount] [player]", NamedTextColor.RED));
            return true;
        }

        String itemName = args[0].toUpperCase();
        int amount = 1;
        Player target = null;

        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
                if (amount < 1 || amount > 64) {
                    sender.sendMessage(Component.text("Amount must be between 1 and 64!", NamedTextColor.RED));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Invalid amount: " + args[1], NamedTextColor.RED));
                return true;
            }
        }

        if (args.length >= 3) {
            target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found: " + args[2], NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player p) {
            target = p;
        } else {
            sender.sendMessage(Component.text("Usage: /giveitem <item> [amount] <player>", NamedTextColor.RED));
            return true;
        }

        ItemStack item;
        String itemDisplay;

        if (itemName.equals("REACH_BOTTLE")) {
            item = plugin.getReachManager().createReachBottle();
            item.setAmount(amount);
            itemDisplay = "Reach Bottle";
        } else if (itemName.equals("REROLL_ITEM")) {
            item = plugin.getReachManager().createRerollItem();
            item.setAmount(amount);
            itemDisplay = "Ability Reroll";
        } else {
            try {
                Material mat = Material.valueOf(itemName);
                item = new ItemStack(mat, amount);
                itemDisplay = mat.name();
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text(
                        "Invalid item: " + itemName + ". Use a valid material name, REACH_BOTTLE, or REROLL_ITEM.",
                        NamedTextColor.RED));
                return true;
            }
        }

        target.getInventory().addItem(item);
        sender.sendMessage(Component.text("Gave " + amount + " " + itemDisplay + " to " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text("You received " + amount + " " + itemDisplay, NamedTextColor.GREEN));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String input = args[0].toUpperCase();
            completions.add("REACH_BOTTLE");
            completions.add("REROLL_ITEM");
            for (Material mat : Material.values()) {
                if (mat.isItem() && mat.name().startsWith(input)) {
                    completions.add(mat.name());
                }
            }
        } else if (args.length == 3) {
            String input = args[2].toLowerCase();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(input)) completions.add(p.getName());
            }
        }
        return completions;
    }
}
