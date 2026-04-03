package com.reachsmp;

import com.reachsmp.commands.*;
import com.reachsmp.listeners.*;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class ReachSMPPlugin extends JavaPlugin {

    private boolean smpStarted = false;
    private boolean gracePeriod = false;
    private BossBar graceBossBar = null;

    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private ReachManager reachManager;
    private AbilityManager abilityManager;
    private HitBarManager hitBarManager;

    private final File stateFile = new File(getDataFolder(), "smpstate.yml");

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();

        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);
        reachManager = new ReachManager(this);
        abilityManager = new AbilityManager(this);
        hitBarManager = new HitBarManager(this);

        loadSMPState();

        // Register listeners
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new CombatListener(this), this);
        pm.registerEvents(new ReachBottleListener(this), this);
        pm.registerEvents(new ItemProtectionListener(this), this);
        pm.registerEvents(new RerollListener(this), this);

        // Register commands
        var fillBarCmd = new FillBarCommand(this);
        getCommand("fillbar").setExecutor(fillBarCmd);

        getCommand("info").setExecutor(new InfoCommand(this));
        getCommand("startreachsmp").setExecutor(new StartReachSMPCommand(this));
        getCommand("stopreachsmp").setExecutor(new StopReachSMPCommand(this));
        getCommand("reachreload").setExecutor(new ReachReloadCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));

        var setReachCmd = new SetReachCommand(this);
        getCommand("setreach").setExecutor(setReachCmd);
        getCommand("setreach").setTabCompleter(setReachCmd);

        var setReachLevelCmd = new SetReachLevelCommand(this);
        getCommand("setreachlevel").setExecutor(setReachLevelCmd);
        getCommand("setreachlevel").setTabCompleter(setReachLevelCmd);

        var giveItemCmd = new GiveItemCommand(this);
        getCommand("giveitem").setExecutor(giveItemCmd);
        getCommand("giveitem").setTabCompleter(giveItemCmd);

        // Auto-save task
        long saveInterval = configManager.getAutoSaveInterval();
        getServer().getScheduler().runTaskTimer(this, () -> playerDataManager.saveAll(), saveInterval, saveInterval);

        getLogger().info("ReachSMP Plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Save play time for online players
        for (Player p : getServer().getOnlinePlayers()) {
            var data = playerDataManager.getOrCreate(p.getUniqueId());
            long sessionTime = System.currentTimeMillis() - data.getJoinTimestamp();
            data.addPlayTime(sessionTime);
        }
        playerDataManager.saveAll();
        saveSMPState();
        getLogger().info("ReachSMP Plugin disabled!");
    }

    private void loadSMPState() {
        if (!stateFile.exists()) return;
        var config = YamlConfiguration.loadConfiguration(stateFile);
        smpStarted = config.getBoolean("smpStarted", false);
        gracePeriod = config.getBoolean("gracePeriod", false);
    }

    private void saveSMPState() {
        var config = new YamlConfiguration();
        config.set("smpStarted", smpStarted);
        config.set("gracePeriod", gracePeriod);
        try {
            config.save(stateFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save SMP state: " + e.getMessage());
        }
    }

    // ---- Getters & Setters ----

    public ConfigManager getConfigManager() { return configManager; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public ReachManager getReachManager() { return reachManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public HitBarManager getHitBarManager() { return hitBarManager; }

    public boolean isSMPStarted() { return smpStarted; }
    public void setSMPStarted(boolean smpStarted) { this.smpStarted = smpStarted; }

    public boolean isGracePeriod() { return gracePeriod; }
    public void setGracePeriod(boolean gracePeriod) { this.gracePeriod = gracePeriod; }

    public BossBar getGraceBossBar() { return graceBossBar; }
    public void setGraceBossBar(BossBar bar) { this.graceBossBar = bar; }
}
