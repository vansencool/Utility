package dev.vansen.utility.inventory;

import dev.vansen.utility.inventory.events.InventoryClickEventHandler;
import dev.vansen.utility.inventory.events.InventoryCloseEventHandler;
import dev.vansen.utility.inventory.events.InventoryDragEventHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(@NotNull Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(@NotNull String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder name(@NotNull Component name) {
        meta.displayName(name);
        return this;
    }

    public ItemBuilder lore(@NotNull String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder lore(@NotNull Component... lore) {
        meta.lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder onClick(@NotNull Consumer<InventoryClickEvent> clickAction) {
        InventoryClickEventHandler.addClickAction(item, clickAction);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    public static void onClose(@NotNull Consumer<InventoryCloseEvent> closeAction) {
        InventoryCloseEventHandler.addCloseAction(closeAction);
    }

    public static void onDrag(@NotNull Consumer<InventoryDragEvent> dragAction) {
        InventoryDragEventHandler.addDragAction(dragAction);
    }
}