package com.reachsmp;

import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public enum ReachVariant {
    STRENGTH("Strength", PotionEffectType.STRENGTH),
    RESISTANCE("Resistance", PotionEffectType.RESISTANCE),
    SPEED("Speed", PotionEffectType.SPEED),
    REGENERATION("Regeneration", PotionEffectType.REGENERATION),
    HEALTH("Health", null),
    FIRE_RESISTANCE("Fire Resistance", PotionEffectType.FIRE_RESISTANCE);

    private final String displayName;
    private final PotionEffectType permanentEffect;

    ReachVariant(String displayName, PotionEffectType permanentEffect) {
        this.displayName = displayName;
        this.permanentEffect = permanentEffect;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PotionEffectType getPermanentEffect() {
        return permanentEffect;
    }

    public static ReachVariant random() {
        ReachVariant[] values = values();
        return values[new Random().nextInt(values.length)];
    }

    /**
     * Returns a random variant that is different from the given one.
     */
    public static ReachVariant randomExcluding(ReachVariant current) {
        ReachVariant[] values = values();
        Random rng = new Random();
        ReachVariant pick;
        do {
            pick = values[rng.nextInt(values.length)];
        } while (pick == current);
        return pick;
    }
}
