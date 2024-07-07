package dev.vansen.utility.inventory.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import static dev.vansen.utility.inventory.InventoryUtils.playerInventoryMap;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        playerInventoryMap.remove((Player) event.getPlayer());
        InventoryCloseEventHandler.handle(event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryClickEventHandler.handle(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryDragEventHandler.handle(event);
    }
}
