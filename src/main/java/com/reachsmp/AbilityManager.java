package com.reachsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class AbilityManager {

    private final ReachSMPPlugin plugin;

    public AbilityManager(ReachSMPPlugin plugin) {
        this.plugin = plugin;
    }

    public void applyPermanentEffects(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
        if (data.isVariantLost()) return;

        ReachVariant variant = data.getVariant();
        if (variant == null) return;

        ConfigManager config = plugin.getConfigManager();

        switch (variant) {
            case RESISTANCE -> player.addPotionEffect(
                    new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0, false, false, false));
            case SPEED -> {
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, config.getSpeedAmplifier() - 1, false, false, false));
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, config.getHasteAmplifier() - 1, false, false, false));
            }
            case REGENERATION -> player.addPotionEffect(
                    new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false, false));
            case HEALTH -> {
                var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    maxHealthAttr.setBaseValue(config.getHealthPermanentMax());
                }
            }
            default -> {}
        }
    }

    public void removePermanentEffects(Player player) {
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.HASTE);
        player.removePotionEffect(PotionEffectType.REGENERATION);
        var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealthAttr != null) {
            maxHealthAttr.setBaseValue(20.0);
        }
    }

    public void activateAbility(Player player, PlayerData data) {
        if (data.isVariantLost()) return;
        ReachVariant variant = data.getVariant();
        if (variant == null) return;

        ConfigManager config = plugin.getConfigManager();

        // Show title
        Component title = Component.text("ABILITY ACTIVATED", NamedTextColor.GOLD, TextDecoration.BOLD);
        Component subtitle = Component.text(variant.getDisplayName(), NamedTextColor.YELLOW);
        player.showTitle(Title.title(title, subtitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

        switch (variant) {
            case STRENGTH -> {
                // Strength II (amplifier 1 = Strength II)
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,
                        config.getStrengthDuration(), config.getStrengthAmplifier(), false, true, true));
                scheduleReapplyPermanent(player, config.getStrengthDuration());
            }
            case RESISTANCE -> {
                // Resistance II (amplifier 1 = Resistance II)
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,
                        config.getResistanceDuration(), config.getResistanceAmplifier(), false, true, true));
                scheduleReapplyPermanent(player, config.getResistanceDuration());
            }
            case SPEED -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,
                        config.getSpeedDuration(), config.getSpeedAmplifier(), false, true, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE,
                        config.getSpeedDuration(), config.getHasteAmplifier(), false, true, true));
                scheduleReapplyPermanent(player, config.getSpeedDuration());
            }
            case REGENERATION -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,
                        config.getSpeedDuration(), 1, false, true, true));
                scheduleReapplyPermanent(player, config.getSpeedDuration());
            }
            case HEALTH -> {
                var maxHealthAttr = player.getAttribute(Attribute.MAX_HEALTH);
                if (maxHealthAttr != null) {
                    double permanentMax = config.getHealthPermanentMax();
                    double activatedMax = config.getHealthActivatedMax(); // 30.0 = 15 hearts

                    maxHealthAttr.setBaseValue(activatedMax);
                    // Do NOT heal the player - just extend the health bar
                    // (no setHealth call = hitbar does not heal)
                    player.sendMessage(Component.text("Health ability activated! Max health extended.", NamedTextColor.GREEN));

                    int durationTicks = config.getHealthActivatedDurationSeconds() * 20;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline()) return;
                            PlayerData freshData = plugin.getPlayerDataManager().getOrCreate(player.getUniqueId());
                            if (freshData.isVariantLost()) return;
                            maxHealthAttr.setBaseValue(permanentMax);
                            // Clamp health so it doesn't exceed new max
                            if (player.getHealth() > permanentMax) {
                                player.setHealth(permanentMax);
                            }
                        }
                    }.runTaskLater(plugin, durationTicks);
                }
            }
            case FIRE_RESISTANCE -> {
                double radius = config.getFireResistanceRadius();
                int fireTicks = config.getFireResistanceFireTicks();
                for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    if (entity.equals(player)) continue;
                    if (entity instanceof Player target) {
                        target.setFireTicks(fireTicks);
                        target.sendMessage(Component.text(
                                "You have been set on fire by " + player.getName() + "!", NamedTextColor.RED));
                    }
                }
                player.sendMessage(Component.text("Nearby players set on fire!", NamedTextColor.GOLD));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) applyPermanentEffects(player);
                    }
                }.runTaskLater(plugin, 20L);
            }
        }
    }

    private void scheduleReapplyPermanent(Player player, int durationTicks) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) applyPermanentEffects(player);
            }
        }.runTaskLater(plugin, durationTicks);
    }
}
