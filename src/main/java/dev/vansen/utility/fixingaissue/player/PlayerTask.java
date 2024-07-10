package dev.vansen.utility.fixingaissue.player;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerTask {
    void run(Player player);
}