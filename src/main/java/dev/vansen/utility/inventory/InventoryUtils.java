package dev.vansen.utility.inventory;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.inventory.events.InventoryListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class InventoryUtils implements Listener {
    private static final Utility plugin = PluginHolder.getPluginInstance();
    public static final Map<Player, Inventory> playerInventoryMap = new HashMap<>();

    public static Inventory createInventory(@NotNull Player player, int size, @NotNull String name) {
        Inventory inventory = Bukkit.createInventory(player, size, Component.text(name));
        playerInventoryMap.put(player, inventory);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
        return inventory;
    }

    public static Inventory createInventory(@NotNull Player player, int size, @NotNull Component name) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        playerInventoryMap.put(player, inventory);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
        return inventory;
    }
}