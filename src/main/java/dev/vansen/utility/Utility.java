package dev.vansen.utility;

import dev.vansen.utility.commands.CommandsManager;
import dev.vansen.utility.fixingaissue.scheduler.TaskUtils;
import dev.vansen.utility.listeners.ListenerRegister;
import dev.vansen.utility.resource.ResourceUtils;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Utility extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            getClass().getDeclaredMethod("onStart");
            onStart();
        } catch (NoSuchMethodException ignored) {

        }
        ResourceUtils.saveYmlFiles();
        new CommandsManager();
        ListenerRegister.registerListeners();
    }

    @Override
    public void onDisable() {
        try {
            getClass().getDeclaredMethod("onStop");
            onStop();
        } catch (NoSuchMethodException ignored) {

        }

        TaskUtils.getScheduler().cancelAll();
    }

    @Override
    public void onLoad() {
        try {
            getClass().getDeclaredMethod("onL");
            onL();
        } catch (NoSuchMethodException ignored) {

        }
    }

    protected void onStart() {

    }

    protected void onL() {

    }

    protected void onStop() {

    }
}