package dev.vansen.utility;

public class PluginHolder {
    private static Utility pluginInstance;

    public static void setPluginInstance(Utility plugin) {
        pluginInstance = plugin;
    }

    public static Utility getPluginInstance() {
        return pluginInstance;
    }
}