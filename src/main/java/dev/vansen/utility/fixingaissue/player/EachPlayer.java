package dev.vansen.utility.fixingaissue.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EachPlayer {

    public static void forEachPlayer(@NotNull PlayerTask task) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            task.run(player);
        }
    }

    public static void forNumPlayers(int num, @NotNull PlayerTask task) {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(players);
        players.stream().limit(num).forEach(task::run);
    }
}