package com.customdespawn.commands;

import com.customdespawn.CustomDespawnPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CustomDespawnCommand implements CommandExecutor, TabCompleter {

    private final CustomDespawnPlugin plugin;

    public CustomDespawnCommand(CustomDespawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("customdespawn.reload") && !sender.isOp()) {
                sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
                return true;
            }

            plugin.getPluginConfig().loadConfig();
            sender.sendMessage(Component.text("CustomDespawn configuration has been successfully reloaded!", NamedTextColor.GREEN));
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("customdespawn.reload") || sender.isOp()) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}