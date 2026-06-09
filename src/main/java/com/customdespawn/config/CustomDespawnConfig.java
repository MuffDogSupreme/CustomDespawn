package com.customdespawn.config;

import com.customdespawn.CustomDespawnPlugin;
import com.customdespawn.util.TimeParser;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomDespawnConfig {

    private final CustomDespawnPlugin plugin;
    private FileConfiguration config;

    private long defaultDespawnTimeTicks;
    private boolean honorPlayerDeathDrops;
    private long deathDropDespawnTimeTicks;
    private Set<String> blacklistedWorlds;
    private Map<String, CustomRule> customRules;

    public CustomDespawnConfig(CustomDespawnPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.defaultDespawnTimeTicks = TimeParser.parseDurationStringToTicks(config.getString("default-despawn-time", "1m"));
        this.honorPlayerDeathDrops = config.getBoolean("honor-player-death-drops", true);
        this.deathDropDespawnTimeTicks = TimeParser.parseDurationStringToTicks(config.getString("death-drop-despawn-time", "5m"));
        this.blacklistedWorlds = config.getStringList("blacklisted-worlds").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        this.customRules = loadCustomRules();

        plugin.getLogger().info("Configuration loaded. Default despawn time: " + defaultDespawnTimeTicks + " ticks.");
    }

    private Map<String, CustomRule> loadCustomRules() {
        Map<String, CustomRule> rules = new HashMap<>();
        ConfigurationSection customRulesSection = config.getConfigurationSection("custom-rules");
        if (customRulesSection == null) {
            return Collections.emptyMap();
        }

        for (String key : customRulesSection.getKeys(false)) {
            ConfigurationSection ruleSection = customRulesSection.getConfigurationSection(key);
            if (ruleSection == null) continue;

            String timeString = ruleSection.getString("time");
            List<String> materialNames = ruleSection.getStringList("materials");

            if (timeString == null || materialNames.isEmpty()) {
                plugin.getLogger().warning("Invalid custom rule '" + key + "'. Missing time or materials.");
                continue;
            }

            long timeTicks = TimeParser.parseDurationStringToTicks(timeString);
            Set<Material> materials = materialNames.stream()
                    .map(name -> {
                        try {
                            return Material.valueOf(name.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid material '" + name + "' in custom rule '" + key + "'. Skipping.");
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!materials.isEmpty()) {
                rules.put(key, new CustomRule(timeTicks, materials));
            }
        }
        return rules;
    }

    public long getDefaultDespawnTimeTicks() {
        return defaultDespawnTimeTicks;
    }

    public boolean isHonorPlayerDeathDrops() {
        return honorPlayerDeathDrops;
    }

    public long getDeathDropDespawnTimeTicks() {
        return deathDropDespawnTimeTicks;
    }

    public boolean isWorldBlacklisted(String worldName) {
        return blacklistedWorlds.contains(worldName.toLowerCase());
    }

    public Map<String, CustomRule> getCustomRules() {
        return customRules;
    }

    public String getDefaultDespawnTimeString() {
        return config.getString("default-despawn-time", "1m");
    }

    public static class CustomRule {
        private final long timeTicks;
        private final Set<Material> materials;

        public CustomRule(long timeTicks, Set<Material> materials) {
            this.timeTicks = timeTicks;
            this.materials = materials;
        }

        public long getTimeTicks() {
            return timeTicks;
        }

        public Set<Material> getMaterials() {
            return materials;
        }
    }
}