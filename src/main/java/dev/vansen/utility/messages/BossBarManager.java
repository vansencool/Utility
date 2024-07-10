package dev.vansen.utility.messages;

import dev.vansen.utility.component.Deserializer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossBarManager {
    private static final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public static void createBossBar(@NotNull Player player, @NotNull String title, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay, float progress, @NotNull Set<BossBar.Flag> flags) {
        Component titleComponent = Deserializer.deserialize(title);
        BossBar bossBar = BossBar.bossBar(titleComponent, progress, color, overlay, flags);
        playerBossBars.put(player.getUniqueId(), bossBar);
        player.showBossBar(bossBar);
    }

    public static void removeBossBar(@NotNull Player player) {
        UUID playerUUID = player.getUniqueId();
        BossBar bossBar = playerBossBars.remove(playerUUID);
        player.hideBossBar(bossBar);
    }

    public static void updateBossBarTitle(@NotNull Player player, @NotNull String newTitle) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            Component titleComponent = Deserializer.deserialize(newTitle);
            bossBar.name(titleComponent);
        }
    }

    public static void updateBossBarProgress(@NotNull Player player, float newProgress) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.progress(newProgress);
        }
    }

    public static void updateBossBarColor(@NotNull Player player, @NotNull BossBar.Color newColor) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.color(newColor);
        }
    }

    public static void updateBossBarOverlay(@NotNull Player player, @NotNull BossBar.Overlay newOverlay) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.overlay(newOverlay);
        }
    }

    public static void addBossBarFlag(@NotNull Player player, @NotNull BossBar.Flag flag) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.addFlag(flag);
        }
    }

    public static void removeBossBarFlag(@NotNull Player player, @NotNull BossBar.Flag flag) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar != null) {
            bossBar.removeFlag(flag);
        }
    }

    public static class Builder {
        private Player player;
        private String title;
        private BossBar.Color color = BossBar.Color.WHITE;
        private BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
        private float progress = 1.0f;
        private final Set<BossBar.Flag> flags = new HashSet<>();

        public Builder player(@NotNull Player player) {
            this.player = player;
            return this;
        }

        public Builder title(@NotNull String title) {
            this.title = title;
            return this;
        }

        public Builder color(@NotNull BossBar.Color color) {
            this.color = color;
            return this;
        }

        public Builder overlay(@NotNull BossBar.Overlay overlay) {
            this.overlay = overlay;
            return this;
        }

        public Builder progress(float progress) {
            this.progress = progress;
            return this;
        }

        public Builder flag(@NotNull BossBar.Flag flag) {
            this.flags.add(flag);
            return this;
        }

        public void create() {
            BossBarManager.createBossBar(player, title, color, overlay, progress, flags);
        }
    }
}