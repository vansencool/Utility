package dev.vansen.utility.tasks.scheduler;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.tc.TimeConverter;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
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
            long ticks = convertMillisToTicks(TimeConverter.convertToMillis(timeUnit.name(), delay));
            if (async) {
                return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
            } else {
                return Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
            }
        }

        public BukkitTask runLater(@NotNull Runnable task, @NotNull String timeUnit, long duration) {
            long delay = TimeConverter.convertToMillis(timeUnit, duration);
            return runLater(task, delay, TimeUnit.MILLISECONDS);
        }

        public BukkitTask runTimer(@NotNull Runnable task, long delay, long period, @NotNull TimeUnit timeUnit) {
            long delayTicks = convertMillisToTicks(TimeConverter.convertToMillis(timeUnit.name(), delay));
            long periodTicks = convertMillisToTicks(TimeConverter.convertToMillis(timeUnit.name(), period));
            if (async) {
                return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
            } else {
                return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
            }
        }

        public BukkitTask runTimer(@NotNull Runnable task, @NotNull String timeUnit, long delay, long period) {
            long delayMillis = TimeConverter.convertToMillis(timeUnit, delay);
            long periodMillis = TimeConverter.convertToMillis(timeUnit, period);
            return runTimer(task, delayMillis, periodMillis, TimeUnit.MILLISECONDS);
        }

        public CompletableFuture<Void> when(@NotNull String taskId, @NotNull String timeFormat) {
            return CompletableFuture.runAsync(() -> {
                long delayMillis = TimeConverter.convertToMillis(timeFormat, Long.parseLong(taskId));
                long delayTicks = convertMillisToTicks(delayMillis);
                String formattedTime = formatTime(timeFormat, delayMillis);
                plugin.getLogger().log(Level.INFO, "Task " + taskId + " will run in " + formattedTime);
            });
        }

        public void cancelAll() {
            Bukkit.getScheduler().cancelTasks(plugin);
        }

        public void cancelTask(@NotNull BukkitTask task) {
            task.cancel();
        }

        public void repeatForever(@NotNull Runnable task, long period, @NotNull TimeUnit timeUnit) {
            runTimer(task, 0, period, timeUnit);
        }

        public void repeatForUnit(@NotNull Runnable task, @NotNull String timeUnit, long duration, long period) {
            long delay = TimeConverter.convertToMillis(timeUnit, duration);
            runTimer(task, delay, period, TimeUnit.MILLISECONDS);
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
                case PLAYER_PERMISSION ->
                        runForPlayers(player -> predicate.test(player.hasPermission("some.permission")), task, playerCount);
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

        private void runForPlayers(Predicate<Player> predicate, Runnable task, int playerCount) {
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