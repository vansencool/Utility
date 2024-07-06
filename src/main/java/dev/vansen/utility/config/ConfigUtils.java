package dev.vansen.utility.config;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.plugin.PluginUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ConfigUtils {
    private final Utility plugin;
    private FileConfiguration config;
    private File configFile;

    private ConfigUtils(@NotNull String fileName) {
        this.plugin = PluginHolder.getPluginInstance();
        this.configFile = new File(PluginUtils.pluginFolder(), fileName);
    }

    public static CompletableFuture<ConfigUtils> load(@NotNull String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigUtils configUtils = new ConfigUtils(fileName);
            configUtils.config = YamlConfiguration.loadConfiguration(configUtils.configFile);
            return configUtils;
        });
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::save);
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public CompletableFuture<Void> reloadAsync() {
        return CompletableFuture.runAsync(this::reload);
    }

    public <T> T get(@NotNull String path) {
        return (T) config.get(path);
    }

    public void set(@NotNull String path, @NotNull Object value) {
        config.set(path, value);
    }

    public void setIf(@NotNull String path, @NotNull Object value, @NotNull Predicate<Object> condition) {
        Object currentValue = config.get(path);
        if (condition.test(currentValue)) {
            config.set(path, value);
        }
    }

    public static class Builder {

        private final ConfigUtils configUtils;

        private Builder(@NotNull String fileName) {
            this.configUtils = new ConfigUtils(fileName);
        }

        public static CompletableFuture<Builder> of(@NotNull String fileName) {
            return CompletableFuture.supplyAsync(() -> {
                Builder builder = new Builder(fileName);
                builder.configUtils.config = YamlConfiguration.loadConfiguration(builder.configUtils.configFile);
                return builder;
            });
        }

        public Builder save() {
            configUtils.save();
            return this;
        }

        public CompletableFuture<Builder> saveAsync() {
            return CompletableFuture.runAsync(configUtils::save).thenApply(v -> this);
        }

        public Builder reload() {
            configUtils.reload();
            return this;
        }

        public CompletableFuture<Builder> reloadAsync() {
            return CompletableFuture.runAsync(configUtils::reload).thenApply(v -> this);
        }

        public <T> T get(@NotNull String path) {
            return configUtils.get(path);
        }

        public Builder set(@NotNull String path, @NotNull Object value) {
            configUtils.set(path, value);
            return this;
        }

        public Builder setIf(@NotNull String path, @NotNull Object value, @NotNull Predicate<Object> condition) {
            configUtils.setIf(path, value, condition);
            return this;
        }

        public ConfigUtils build() {
            return configUtils;
        }
    }
}