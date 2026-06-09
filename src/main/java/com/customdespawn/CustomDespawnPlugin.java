package com.customdespawn;

import com.customdespawn.commands.CustomDespawnCommand;
import com.customdespawn.config.CustomDespawnConfig;
import com.customdespawn.listeners.ItemSpawnListener;
import com.customdespawn.listeners.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDespawnPlugin extends JavaPlugin {

    private static CustomDespawnPlugin instance;
    private CustomDespawnConfig pluginConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        pluginConfig = new CustomDespawnConfig(this);

        getServer().getPluginManager().registerEvents(new ItemSpawnListener(this, pluginConfig), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(pluginConfig), this);

        CustomDespawnCommand executor = new CustomDespawnCommand(this);
        getCommand("customdespawn").setExecutor(executor);
        getCommand("customdespawn").setTabCompleter(executor);

        getLogger().info("CustomDespawn has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomDespawn has been disabled!");
    }

    public static CustomDespawnPlugin getInstance() {
        return instance;
    }

    public CustomDespawnConfig getPluginConfig() {
        return pluginConfig;
    }
}