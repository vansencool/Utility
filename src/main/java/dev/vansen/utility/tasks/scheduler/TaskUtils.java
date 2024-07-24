package dev.vansen.utility.tasks.scheduler;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskUtils {

    private static final Utility plugin = PluginHolder.getPluginInstance();

    public static Scheduler getScheduler() {
        return new Scheduler(false);
    }

    public static Scheduler getAsyncScheduler() {
        return new Scheduler(true);
    }

    public static class Scheduler {
        private final boolean async;

        private Scheduler(boolean async) {
            this.async = async;
        }

        public void run(@NotNull Runnable task) {
            if (async) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            } else {
                Bukkit.getScheduler().runTask(plugin, task);
            }
        }

        public BukkitTask runLater(@NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
            long delayMillis = timeUnit.toMillis(delay);
            long ticks = convertMillisToTicks(delayMillis);
            if (async) {
                return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
            } else {
                return Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
            }
        }

        public BukkitTask runTimer(@NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
            long delayMillis = timeUnit.toMillis(delay);
            long periodMillis = timeUnit.toMillis(period);
            long delayTicks = convertMillisToTicks(delayMillis);
            long periodTicks = convertMillisToTicks(periodMillis);
            if (async) {
                return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
            } else {
                return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
            }
        }

        public void repeatForever(@NotNull Runnable task, long period, @NotNull TimeUnit timeUnit) {
            runTimer(task, 0, period, timeUnit);
        }

        public void repeatForUnit(@NotNull Runnable task, long duration, @NotNull TimeUnit durationUnit, long period, @NotNull TimeUnit periodUnit) {
            long durationMillis = durationUnit.toMillis(duration);
            long periodMillis = periodUnit.toMillis(period);
            runTimer(task, durationMillis, periodMillis, TimeUnit.MILLISECONDS);
        }

        public void runConditional(@NotNull Runnable task, @NotNull TaskCondition condition, @NotNull Predicate<Object> predicate, int playerCount) {
            switch (condition) {
                case PLAYER_KILLS ->
                        runForPlayers(player -> predicate.test(player.getStatistic(Statistic.PLAYER_KILLS)), task, playerCount);
                case PLAYER_DEATHS ->
                        runForPlayers(player -> predicate.test(player.getStatistic(Statistic.DEATHS)), task, playerCount);
                case PLAYER_JUMPS ->
                        runForPlayers(player -> predicate.test(player.getStatistic(Statistic.JUMP)), task, playerCount);
                case PLAYER_PLAYTIME ->
                        runForPlayers(player -> predicate.test(player.getStatistic(Statistic.PLAY_ONE_MINUTE)), task, playerCount);
                case SERVER_TPS -> {
                    double tps = plugin.getServer().getTPS()[0];
                    if (predicate.test(tps)) {
                        run(task);
                    }
                }
                case SERVER_PLAYER_COUNT -> {
                    int count = plugin.getServer().getOnlinePlayers().size();
                    if (predicate.test(count)) {
                        run(task);
                    }
                }
            }
        }

        public void runForPlayers(@NotNull Predicate<Player> predicate, @NotNull Runnable task, int playerCount) {
            var players = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(predicate)
                    .limit(playerCount == -1 ? Long.MAX_VALUE : playerCount)
                    .collect(Collectors.toList());
            players.forEach(player -> run(task));
        }

        private long convertMillisToTicks(long millis) {
            return millis / 50;
        }

        private String formatTime(@NotNull String timeFormat, long millis) {
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long months = days / 30;
            long years = days / 365;

            seconds %= 60;
            minutes %= 60;
            hours %= 24;
            days %= 30;
            months %= 12;

            return timeFormat.replace("<y>", String.valueOf(years))
                    .replace("<mo>", String.valueOf(months))
                    .replace("<d>", String.valueOf(days))
                    .replace("<h>", String.valueOf(hours))
                    .replace("<m>", String.valueOf(minutes))
                    .replace("<s>", String.valueOf(seconds));
        }
    }
}