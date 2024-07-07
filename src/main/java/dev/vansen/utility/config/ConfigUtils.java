package dev.vansen.utility.config;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.plugin.PluginUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ConfigUtils {
    private final Utility plugin;
    private FileConfiguration config;
    private final File configFile;

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

    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public Set<String> getKeys(@NotNull String path, boolean deep) {
        return config.getConfigurationSection(path).getKeys(deep);
    }

    public <T> T get(@NotNull String path) {
        return (T) config.get(path);
    }

    public String getString(@NotNull String path) {
        return config.getString(path);
    }

    public List<String> getStringList(@NotNull String path) {
        return config.getStringList(path);
    }

    public List<?> getList(@NotNull String path) {
        return config.getList(path);
    }

    public int getInt(@NotNull String path) {
        return config.getInt(path);
    }

    public double getDouble(@NotNull String path) {
        return config.getDouble(path);
    }

    public boolean getBoolean(@NotNull String path) {
        return config.getBoolean(path);
    }

    public UUID getUUID(@NotNull String path) {
        String uuidString = config.getString(path);
        return uuidString != null ? UUID.fromString(uuidString) : null;
    }

    public Location getLocation(@NotNull String path) {
        return config.getLocation(path);
    }

    public Material getMaterial(@NotNull String path) {
        String materialString = config.getString(path);
        return materialString != null ? Material.valueOf(materialString) : null;
    }

    public ItemStack getItemStack(@NotNull String path) {
        return config.getItemStack(path);
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

        public Set<String> getKeys(boolean deep) {
            return configUtils.getKeys(deep);
        }

        public Set<String> getKeys(@NotNull String path, boolean deep) {
            return configUtils.getKeys(path, deep);
        }

        public <T> T get(@NotNull String path) {
            return configUtils.get(path);
        }

        public String getString(@NotNull String path) {
            return configUtils.getString(path);
        }

        public List<String> getStringList(@NotNull String path) {
            return configUtils.getStringList(path);
        }

        public List<?> getList(@NotNull String path) {
            return configUtils.getList(path);
        }

        public int getInt(@NotNull String path) {
            return configUtils.getInt(path);
        }

        public double getDouble(@NotNull String path) {
            return configUtils.getDouble(path);
        }

        public boolean getBoolean(@NotNull String path) {
            return configUtils.getBoolean(path);
        }

        public UUID getUUID(@NotNull String path) {
            return configUtils.getUUID(path);
        }

        public Location getLocation(@NotNull String path) {
            return configUtils.getLocation(path);
        }

        public Material getMaterial(@NotNull String path) {
            return configUtils.getMaterial(path);
        }

        public ItemStack getItemStack(@NotNull String path) {
            return configUtils.getItemStack(path);
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