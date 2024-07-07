package dev.vansen.utility.inventory.events;

import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryDragEventHandler {

    private static final List<Consumer<InventoryDragEvent>> dragActions = new ArrayList<>();

    public static void addDragAction(Consumer<InventoryDragEvent> action) {
        dragActions.add(action);
    }

    public static void handle(InventoryDragEvent event) {
        for (Consumer<InventoryDragEvent> action : dragActions) {
            action.accept(event);
        }
    }
}