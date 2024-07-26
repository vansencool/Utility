package dev.vansen.utility.tasks.scheduler;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskUtils {

    private static final Utility plugin = PluginHolder.getPluginInstance();
    private static final Map<String, Long> taskStartTimes = new ConcurrentHashMap<>();
    private static final Map<String, Long> taskDurations = new ConcurrentHashMap<>();
    private static final Map<String, BukkitTask> scheduledTasks = new ConcurrentHashMap<>();

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

        public void run(@NotNull String taskId, @NotNull Runnable task) {
            BukkitTask bukkitTask;
            if (async) {
                bukkitTask = Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
            } else {
                bukkitTask = Bukkit.getScheduler().runTask(plugin, task);
            }
            scheduledTasks.put(taskId, bukkitTask);
            taskStartTimes.put(taskId, System.currentTimeMillis());
        }

        public BukkitTask runLater(@NotNull String taskId, @NotNull Runnable task, long delay, @NotNull TimeUnit timeUnit) {
            long delayMillis = timeUnit.toMillis(delay);
            long ticks = convertMillisToTicks(delayMillis);
            BukkitTask bukkitTask;
            if (async) {
                bukkitTask = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
            } else {
                bukkitTask = Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
            }
            scheduledTasks.put(taskId, bukkitTask);
            taskStartTimes.put(taskId, System.currentTimeMillis() + delayMillis);
            return bukkitTask;
        }

        public void repeatForever(@NotNull String taskId, @NotNull Runnable task, long period, @NotNull TimeUnit timeUnit) {
            runTimer(taskId, task, 0, period, timeUnit);
        }

        public void repeatForUnit(@NotNull String taskId, @NotNull Runnable task, long duration, @NotNull TimeUnit durationUnit, long period, @NotNull TimeUnit periodUnit) {
            long durationMillis = durationUnit.toMillis(duration);
            long periodMillis = periodUnit.toMillis(period);
            long endTime = System.currentTimeMillis() + durationMillis;
            taskStartTimes.put(taskId, System.currentTimeMillis());
            taskDurations.put(taskId, durationMillis);

            BukkitTask bukkitTask = runTimer(taskId, () -> {
                if (System.currentTimeMillis() < endTime) {
                    task.run();
                } else {
                    BukkitTask taskToCancel = scheduledTasks.remove(taskId);
                    if (taskToCancel != null) {
                        Bukkit.getScheduler().cancelTask(taskToCancel.getTaskId());
                    }
                }
            }, 0, periodMillis, TimeUnit.MILLISECONDS);
            scheduledTasks.put(taskId, bukkitTask);
        }

        public void runConditional(@NotNull String taskId, @NotNull Runnable task, @NotNull TaskCondition condition, @NotNull Predicate<Object> predicate, int playerCount) {
            switch (condition) {
                case PLAYER_KILLS ->
                        runForPlayers(taskId, player -> predicate.test(player.getStatistic(Statistic.PLAYER_KILLS)), task, playerCount);
                case PLAYER_DEATHS ->
                        runForPlayers(taskId, player -> predicate.test(player.getStatistic(Statistic.DEATHS)), task, playerCount);
                case PLAYER_JUMPS ->
                        runForPlayers(taskId, player -> predicate.test(player.getStatistic(Statistic.JUMP)), task, playerCount);
                case PLAYER_PLAYTIME ->
                        runForPlayers(taskId, player -> predicate.test(player.getStatistic(Statistic.PLAY_ONE_MINUTE)), task, playerCount);
                case SERVER_TPS -> {
                    double tps = plugin.getServer().getTPS()[0];
                    if (predicate.test(tps)) {
                        run(taskId, task);
                    }
                }
                case SERVER_PLAYER_COUNT -> {
                    int count = plugin.getServer().getOnlinePlayers().size();
                    if (predicate.test(count)) {
                        run(taskId, task);
                    }
                }
            }
        }

        public void runForPlayers(@NotNull String taskId, @NotNull Predicate<Player> predicate, @NotNull Runnable task, int playerCount) {
            var players = Bukkit.getOnlinePlayers()
                    .stream()
                    .filter(predicate)
                    .limit(playerCount == -1 ? Long.MAX_VALUE : playerCount)
                    .collect(Collectors.toList());
            players.forEach(player -> run(taskId, task));
        }

        public String when(@NotNull String taskId, @NotNull String timeFormat) {
            Long startTime = taskStartTimes.get(taskId);
            Long duration = taskDurations.get(taskId);

            if (startTime == null || duration == null) {
                return "Task not found or not scheduled with a duration.";
            }

            long remainingTime = startTime + duration - System.currentTimeMillis();
            return formatTime(timeFormat, remainingTime);
        }

        public void cancelAll() {
            for (BukkitTask task : scheduledTasks.values()) {
                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }
            scheduledTasks.clear();
            taskStartTimes.clear();
            taskDurations.clear();
        }

        public void cancel(@NotNull String taskId) {
            BukkitTask task = scheduledTasks.remove(taskId);
            if (task != null) {
                Bukkit.getScheduler().cancelTask(task.getTaskId());
            }
            taskStartTimes.remove(taskId);
            taskDurations.remove(taskId);
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

        private BukkitTask runTimer(@NotNull String taskId, @NotNull Runnable task, long initialDelay, long period, @NotNull TimeUnit timeUnit) {
            long initialDelayMillis = timeUnit.toMillis(initialDelay);
            long periodMillis = timeUnit.toMillis(period);
            long initialDelayTicks = convertMillisToTicks(initialDelayMillis);
            long periodTicks = convertMillisToTicks(periodMillis);
            BukkitTask bukkitTask;

            if (async) {
                bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, initialDelayTicks, periodTicks);
            } else {
                bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayTicks, periodTicks);
            }
            scheduledTasks.put(taskId, bukkitTask);
            return bukkitTask;
        }
    }
}