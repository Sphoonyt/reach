package com.reachsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ReachManager {

    private final ReachSMPPlugin plugin;

    public ReachManager(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyReach(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        var attr = player.getAttribute(Attribute.GENERIC_ATTACK_RANGE);
        if (attr != null) {
            attr.setBaseValue(data.getReach());
        }
    }

    public ItemStack createReachBottle() {
        ConfigManager config = plugin.getConfigManager();
        ItemStack bottle = new ItemStack(config.getBottleMaterial(), 1);
        ItemMeta meta = bottle.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(config.getBottleName(), NamedTextColor.GREEN)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true));
            meta.lore(List.of(Component.text(config.getBottleLore(), NamedTextColor.GRAY)));
            meta.setEnchantmentGlintOverride(config.isBottleGlowing());
            meta.setFireResistant(true);
            bottle.setItemMeta(meta);
        }
        return bottle;
    }

    public boolean isReachBottle(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        ConfigManager config = plugin.getConfigManager();
        if (item.getType() != config.getBottleMaterial()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String name = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return name.contains(config.getBottleName());
    }

    public ItemStack createRerollItem() {
        ConfigManager config = plugin.getConfigManager();
        ItemStack item = new ItemStack(config.getRerollMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(config.getRerollName(), NamedTextColor.LIGHT_PURPLE)
                    .decoration(TextDecoration.ITALIC, false)
                    .decoration(TextDecoration.BOLD, true));
            meta.lore(List.of(Component.text(config.getRerollLore(), NamedTextColor.GRAY)));
            meta.setEnchantmentGlintOverride(config.isRerollGlowing());
            meta.setFireResistant(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isRerollItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        ConfigManager config = plugin.getConfigManager();
        if (item.getType() != config.getRerollMaterial()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return false;
        String name = PlainTextComponentSerializer.plainText().serialize(meta.displayName());
        return name.contains(config.getRerollName());
    }
}
