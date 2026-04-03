package com.reachsmp;

import java.util.UUID;

public class PlayerData {

    public static final double DEFAULT_REACH = 3.0;
    public static final double MAX_REACH = 6.0;
    public static final double MIN_REACH = 2.0;
    public static final double HIT_BAR_MAX = 100.0;
    public static final double REACH_STEAL = 0.5;

    private final UUID uuid;
    private ReachVariant variant;
    private double reach;
    private int kills;
    private int deaths;
    private long joinTimestamp;
    private long totalPlayTime;
    private double hitBar;
    private boolean variantLost;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.variant = null;
        this.reach = DEFAULT_REACH;
        this.kills = 0;
        this.deaths = 0;
        this.joinTimestamp = System.currentTimeMillis();
        this.totalPlayTime = 0;
        this.hitBar = 0.0;
        this.variantLost = false;
    }

    public UUID getUuid() { return uuid; }

    public ReachVariant getVariant() { return variant; }
    public void setVariant(ReachVariant variant) { this.variant = variant; }

    public double getReach() { return reach; }
    public void setReach(double reach) { this.reach = Math.min(Math.max(reach, MIN_REACH), MAX_REACH); }
    public void addReach(double amount) { setReach(this.reach + amount); }
    public void removeReach(double amount) { setReach(this.reach - amount); }

    public int getKills() { return kills; }
    public void addKill() { this.kills++; }

    public int getDeaths() { return deaths; }
    public void addDeath() { this.deaths++; }

    public long getJoinTimestamp() { return joinTimestamp; }
    public void setJoinTimestamp(long joinTimestamp) { this.joinTimestamp = joinTimestamp; }

    public long getTotalPlayTime() { return totalPlayTime; }
    public void setTotalPlayTime(long totalPlayTime) { this.totalPlayTime = totalPlayTime; }
    public void addPlayTime(long millis) { this.totalPlayTime += millis; }

    public double getHitBar() { return hitBar; }
    public void setHitBar(double hitBar) { this.hitBar = Math.min(Math.max(hitBar, 0), HIT_BAR_MAX); }
    public void addHitBar(double amount) { setHitBar(this.hitBar + amount); }

    public boolean isHitBarFull() { return hitBar >= HIT_BAR_MAX; }

    public boolean isVariantLost() { return variantLost; }
    public void setVariantLost(boolean variantLost) { this.variantLost = variantLost; }

    public boolean canDropReach() { return reach > MIN_REACH; }

    public double getPlayTimeDays() {
        return totalPlayTime / (1000.0 * 60 * 60 * 24);
    }
}
