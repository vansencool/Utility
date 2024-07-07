package dev.vansen.utility.inventory.events;

import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryCloseEventHandler {

    private static final List<Consumer<InventoryCloseEvent>> closeActions = new ArrayList<>();

    public static void addCloseAction(Consumer<InventoryCloseEvent> action) {
        closeActions.add(action);
    }

    public static void handle(InventoryCloseEvent event) {
        for (Consumer<InventoryCloseEvent> action : closeActions) {
            action.accept(event);
        }
    }
}