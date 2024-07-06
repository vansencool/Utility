package dev.vansen.utility.tasks.player;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerTask {
    void run(Player player);
}