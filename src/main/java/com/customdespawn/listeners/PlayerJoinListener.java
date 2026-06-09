package com.customdespawn.listeners;

import com.customdespawn.config.CustomDespawnConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final CustomDespawnConfig config;

    public PlayerJoinListener(CustomDespawnConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugin("CustomDespawn"),
            () -> {
                if (player.isOp()) {
                    String defaultDespawnTimeString = config.getDefaultDespawnTimeString();
                    Component message = Component.text("The item despawn time is overridden by the plugin and is set to ", NamedTextColor.GREEN)
                            .append(Component.text(defaultDespawnTimeString, NamedTextColor.YELLOW));
                    player.sendMessage(message);
                }
            },
            60L
        );
    }
}