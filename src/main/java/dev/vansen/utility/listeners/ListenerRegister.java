package dev.vansen.utility.listeners;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.Utility;
import dev.vansen.utility.annotations.AutoRegister;
import dev.vansen.utility.annotations.OkFunnyEnough;
import dev.vansen.utility.plugin.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.Set;

public class ListenerRegister {

    public static void registerListeners() {
        Utility plugin = PluginHolder.getPluginInstance();
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoRegister.class);
        annotatedClasses.addAll(reflections.getTypesAnnotatedWith(OkFunnyEnough.class));

        for (Class<?> clazz : annotatedClasses) {
            if (Listener.class.isAssignableFrom(clazz)) {
                try {
                    Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                } catch (Exception e) {
                    PluginUtils.getLogger().info("Failed to register listener: " + clazz.getName());
                    e.printStackTrace();
                }
            }
        }
    }
}