package com.reachsmp;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final ReachSMPPlugin plugin;
    private final File dataFile;

    public PlayerDataManager(ReachSMPPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        loadAll();
    }

    public PlayerData getOrCreate(UUID uuid) {
        return playerDataMap.computeIfAbsent(uuid, PlayerData::new);
    }

    public boolean containsKey(UUID uuid) {
        return playerDataMap.containsKey(uuid);
    }

    private void loadAll() {
        if (!dataFile.exists()) return;
        var dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : dataConfig.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                continue;
            }
            PlayerData data = getOrCreate(uuid);
            var section = dataConfig.getConfigurationSection(key);
            if (section == null) continue;

            String variantName = section.getString("variant", "NONE");
            if (!variantName.equals("NONE")) {
                try {
                    data.setVariant(ReachVariant.valueOf(variantName));
                } catch (IllegalArgumentException ignored) {}
            }
            data.setReach(section.getDouble("reach", PlayerData.DEFAULT_REACH));
            data.setHitBar(section.getDouble("hitBar", 0.0));
            data.setVariantLost(section.getBoolean("variantLost", false));
            data.setTotalPlayTime(section.getLong("totalPlayTime", 0L));
            data.setJoinTimestamp(section.getLong("joinTimestamp", System.currentTimeMillis()));
        }
    }

    public void saveAll() {
        var dataConfig = new YamlConfiguration();
        for (Map.Entry<UUID, PlayerData> entry : playerDataMap.entrySet()) {
            String key = entry.getKey().toString();
            PlayerData data = entry.getValue();
            dataConfig.set(key + ".variant", data.getVariant() != null ? data.getVariant().name() : "NONE");
            dataConfig.set(key + ".reach", data.getReach());
            dataConfig.set(key + ".kills", data.getKills());
            dataConfig.set(key + ".deaths", data.getDeaths());
            dataConfig.set(key + ".hitBar", data.getHitBar());
            dataConfig.set(key + ".variantLost", data.isVariantLost());
            dataConfig.set(key + ".totalPlayTime", data.getTotalPlayTime());
            dataConfig.set(key + ".joinTimestamp", data.getJoinTimestamp());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public void clearAllVariants() {
        for (PlayerData data : playerDataMap.values()) {
            data.setVariant(null);
            data.setVariantLost(false);
            data.setHitBar(0.0);
        }
    }

    public void assignRandomVariantsToAll() {
        for (PlayerData data : playerDataMap.values()) {
            if (data.getVariant() == null) {
                data.setVariant(ReachVariant.random());
            }
        }
    }

    public Collection<PlayerData> getAll() {
        return playerDataMap.values();
    }

    public PlayerData random() {
        List<PlayerData> list = new ArrayList<>(playerDataMap.values());
        if (list.isEmpty()) return null;
        return list.get(new Random().nextInt(list.size()));
    }
}
