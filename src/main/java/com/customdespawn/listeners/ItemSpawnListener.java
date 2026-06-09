package com.customdespawn.listeners;

import com.customdespawn.CustomDespawnPlugin;
import com.customdespawn.config.CustomDespawnConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSpawnListener implements Listener {

    private static final int VANILLA_DESPAWN_AGE = 6000;

    private final CustomDespawnPlugin plugin;
    private final CustomDespawnConfig config;
    private final NamespacedKey isPlayerDeathDropKey;

    public ItemSpawnListener(CustomDespawnPlugin plugin, CustomDespawnConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.isPlayerDeathDropKey = new NamespacedKey(plugin, "is_player_death_drop");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!config.isHonorPlayerDeathDrops()) {
            return;
        }

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        event.getDrops().clear();

        for (ItemStack itemStack : drops) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            Item itemEntity = event.getEntity().getWorld().dropItemNaturally(
                    event.getEntity().getLocation(), itemStack
            );
            if (itemEntity != null) {
                itemEntity.getPersistentDataContainer().set(
                        isPlayerDeathDropKey, PersistentDataType.BOOLEAN, true
                );
                itemEntity.setTicksLived(VANILLA_DESPAWN_AGE - (int) config.getDeathDropDespawnTimeTicks());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        Material itemType = item.getItemStack().getType();
        String worldName = item.getWorld().getName();

        if (config.isWorldBlacklisted(worldName)) {
            return;
        }

        if (item.getPersistentDataContainer().has(isPlayerDeathDropKey, PersistentDataType.BOOLEAN)) {
            return;
        }

        long despawnTimeTicks = resolveDespawnTimeTicks(itemType);
        item.setTicksLived(VANILLA_DESPAWN_AGE - (int) despawnTimeTicks);
    }

    private long resolveDespawnTimeTicks(Material itemType) {
        for (Map.Entry<String, CustomDespawnConfig.CustomRule> entry : config.getCustomRules().entrySet()) {
            CustomDespawnConfig.CustomRule rule = entry.getValue();
            if (rule.getMaterials().contains(itemType)) {
                return rule.getTimeTicks();
            }
        }
        return config.getDefaultDespawnTimeTicks();
    }
}