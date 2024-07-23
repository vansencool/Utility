package dev.vansen.utility;

import org.jetbrains.annotations.NotNull;

public class PluginHolder {
    private static Utility pluginInstance;

    public static void setPluginInstance(@NotNull Utility plugin) {
        pluginInstance = plugin;
    }

    public static Utility getPluginInstance() {
        return pluginInstance;
    }
}