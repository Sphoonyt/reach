package com.reachsmp;

import org.bukkit.Material;

public class ConfigManager {

    private final ReachSMPPlugin plugin;

    private double defaultReach;
    private double maxReach;
    private double minReach;
    private double reachSteal;
    private double hitBarMax;
    private double damageMultiplier;
    private int barDisplayLength;
    private long barDisplayInterval;
    private long graceDurationMinutes;
    private int worldBorderSize;
    private int worldBorderExpandSeconds;
    private int countdownSeconds;
    private int strengthDuration;
    private int strengthAmplifier;
    private int resistanceDuration;
    private int resistanceAmplifier;
    private int speedDuration;
    private int speedAmplifier;
    private int hasteAmplifier;
    private double healthPermanentMax;
    private double healthActivatedMax;
    private int healthActivatedDurationSeconds;
    private double fireResistanceRadius;
    private int fireResistanceFireTicks;
    private Material bottleMaterial;
    private String bottleName;
    private String bottleLore;
    private boolean bottleGlowing;
    private Material rerollMaterial;
    private String rerollName;
    private String rerollLore;
    private boolean rerollGlowing;
    private long autoSaveInterval;

    public ConfigManager(ReachSMPPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        var config = plugin.getConfig();

        defaultReach = config.getDouble("reach.default", 3.0);
        maxReach = config.getDouble("reach.max", 6.0);
        minReach = config.getDouble("reach.min", 2.0);
        reachSteal = config.getDouble("reach.steal-amount", 0.5);
        hitBarMax = config.getDouble("hit-bar.max", 100.0);
        damageMultiplier = config.getDouble("hit-bar.damage-multiplier", 2.5);
        barDisplayLength = config.getInt("hit-bar.display-length", 20);
        barDisplayInterval = config.getLong("hit-bar.display-interval-ticks", 5L);
        graceDurationMinutes = config.getLong("grace-period.duration-minutes", 30L);
        worldBorderSize = config.getInt("world-border.size", 2000);
        worldBorderExpandSeconds = config.getInt("world-border.expand-seconds", 30);
        countdownSeconds = config.getInt("countdown.seconds", 5);
        strengthDuration = config.getInt("abilities.strength.activated-duration-ticks", 120);
        strengthAmplifier = config.getInt("abilities.strength.activated-amplifier", 1);
        resistanceDuration = config.getInt("abilities.resistance.activated-duration-ticks", 120);
        resistanceAmplifier = config.getInt("abilities.resistance.activated-amplifier", 1);
        speedDuration = config.getInt("abilities.speed.activated-duration-ticks", 120);
        speedAmplifier = config.getInt("abilities.speed.activated-amplifier", 2);
        hasteAmplifier = config.getInt("abilities.speed.haste-amplifier", 2);
        healthPermanentMax = config.getDouble("abilities.health.permanent-max-health", 26.0);
        healthActivatedMax = config.getDouble("abilities.health.activated-max-health", 30.0);
        healthActivatedDurationSeconds = config.getInt("abilities.health.activated-duration-seconds", 45);
        fireResistanceRadius = config.getDouble("abilities.fire-resistance.activated-radius", 8.0);
        fireResistanceFireTicks = config.getInt("abilities.fire-resistance.activated-fire-ticks", 100);

        String matName = config.getString("reach-bottle.material", "EXPERIENCE_BOTTLE");
        try {
            bottleMaterial = Material.valueOf(matName);
        } catch (IllegalArgumentException e) {
            bottleMaterial = Material.EXPERIENCE_BOTTLE;
        }
        bottleName = config.getString("reach-bottle.name", "Reach +0.5");
        bottleLore = config.getString("reach-bottle.lore", "Drink to gain +0.5 reach");
        bottleGlowing = config.getBoolean("reach-bottle.glowing", true);

        String rerollMatName = config.getString("reroll-item.material", "COMPASS");
        try {
            rerollMaterial = Material.valueOf(rerollMatName);
        } catch (IllegalArgumentException e) {
            rerollMaterial = Material.COMPASS;
        }
        rerollName = config.getString("reroll-item.name", "Ability Reroll");
        rerollLore = config.getString("reroll-item.lore", "Right-click to reroll your ability");
        rerollGlowing = config.getBoolean("reroll-item.glowing", true);

        autoSaveInterval = config.getLong("auto-save-interval-ticks", 6000L);
    }

    public double getDefaultReach() { return defaultReach; }
    public double getMaxReach() { return maxReach; }
    public double getMinReach() { return minReach; }
    public double getReachSteal() { return reachSteal; }
    public double getHitBarMax() { return hitBarMax; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public int getBarDisplayLength() { return barDisplayLength; }
    public long getBarDisplayInterval() { return barDisplayInterval; }
    public long getGraceDurationMinutes() { return graceDurationMinutes; }
    public int getWorldBorderSize() { return worldBorderSize; }
    public int getWorldBorderExpandSeconds() { return worldBorderExpandSeconds; }
    public int getCountdownSeconds() { return countdownSeconds; }
    public int getStrengthDuration() { return strengthDuration; }
    public int getStrengthAmplifier() { return strengthAmplifier; }
    public int getResistanceDuration() { return resistanceDuration; }
    public int getResistanceAmplifier() { return resistanceAmplifier; }
    public int getSpeedDuration() { return speedDuration; }
    public int getSpeedAmplifier() { return speedAmplifier; }
    public int getHasteAmplifier() { return hasteAmplifier; }
    public double getHealthPermanentMax() { return healthPermanentMax; }
    public double getHealthActivatedMax() { return healthActivatedMax; }
    public int getHealthActivatedDurationSeconds() { return healthActivatedDurationSeconds; }
    public double getFireResistanceRadius() { return fireResistanceRadius; }
    public int getFireResistanceFireTicks() { return fireResistanceFireTicks; }
    public Material getBottleMaterial() { return bottleMaterial; }
    public String getBottleName() { return bottleName; }
    public String getBottleLore() { return bottleLore; }
    public boolean isBottleGlowing() { return bottleGlowing; }
    public Material getRerollMaterial() { return rerollMaterial; }
    public String getRerollName() { return rerollName; }
    public String getRerollLore() { return rerollLore; }
    public boolean isRerollGlowing() { return rerollGlowing; }
    public long getAutoSaveInterval() { return autoSaveInterval; }
}
