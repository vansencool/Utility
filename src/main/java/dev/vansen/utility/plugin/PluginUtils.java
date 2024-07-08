package dev.vansen.utility.plugin;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

public class PluginUtils {

    private static final Logger logger = PluginHolder.getPluginInstance().getLogger();
    private final static Utility plugin = PluginHolder.getPluginInstance();

    public static File pluginFolder() {
        return plugin.getDataFolder();
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void save(@NotNull String fileName, boolean replace) {
        plugin.saveResource(fileName, replace);
    }

    public static void save(@NotNull String fileName) {
        File file = new File(pluginFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
}