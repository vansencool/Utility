package dev.vansen.utility;

import org.jetbrains.annotations.NotNull;

public class PluginHolder {
    private static Utility pluginInstance;

    public static Utility getPluginInstance() {
        return pluginInstance;
    }

    public static void setPluginInstance(@NotNull Utility plugin) {
        pluginInstance = plugin;
    }
}