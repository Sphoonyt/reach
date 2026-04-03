package com.reachsmp.listeners;

import com.reachsmp.PlayerData;
import com.reachsmp.ReachSMPPlugin;
import com.reachsmp.ReachVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.time.Duration;

public class RerollListener implements Listener {

    private final ReachSMPPlugin plugin;

    public RerollListener(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Only fire once (main hand)
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();

        if (!plugin.getReachManager().isRerollItem(item)) return;

        // Must be right-click (air or block)
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);

        if (!plugin.isSMPStarted()) {
            player.sendMessage(Component.text("The SMP has not started yet!", NamedTextColor.RED));
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());

        if (data.isVariantLost()) {
            player.sendMessage(Component.text("You have lost your ability and cannot reroll!", NamedTextColor.RED));
            return;
        }

        ReachVariant oldVariant = data.getVariant();

        // Pick a new variant different from current
        ReachVariant newVariant = (oldVariant != null)
                ? ReachVariant.randomExcluding(oldVariant)
                : ReachVariant.random();

        // Remove old effects
        plugin.getAbilityManager().removePermanentEffects(player);

        // Assign new variant
        data.setVariant(newVariant);
        data.setHitBar(0.0);

        // Apply new permanent effects
        plugin.getAbilityManager().applyPermanentEffects(player);

        // Consume one item
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() > 0 ? item : null);

        // Show title
        Component title = Component.text("ABILITY REROLLED!", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD);
        Component subtitle = Component.text(newVariant.getDisplayName(), NamedTextColor.WHITE);
        player.showTitle(Title.title(title, subtitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.sendMessage(Component.text(
                "Your ability has been rerolled from "
                        + (oldVariant != null ? oldVariant.getDisplayName() : "None")
                        + " to " + newVariant.getDisplayName() + "!",
                NamedTextColor.LIGHT_PURPLE));
    }
}
