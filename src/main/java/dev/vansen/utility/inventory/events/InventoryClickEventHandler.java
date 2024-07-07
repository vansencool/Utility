package dev.vansen.utility.inventory.events;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryClickEventHandler {

    private static final Map<ItemStack, Consumer<InventoryClickEvent>> clickActions = new HashMap<>();

    public static void addClickAction(ItemStack item, Consumer<InventoryClickEvent> action) {
        clickActions.put(item, action);
    }

    public static void handle(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickActions.containsKey(clickedItem)) {
            clickActions.get(clickedItem).accept(event);
        }
    }
}