package dev.vansen.utility;

import dev.vansen.utility.listeners.ListenerRegister;
import dev.vansen.utility.resource.ResourceUtils;
import dev.vansen.utility.tasks.scheduler.TaskUtils;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Utility extends JavaPlugin {

    private boolean wasReloaded = false;

    @Override
    public void onEnable() {
        if (wasReloaded) {
            try {
                getClass().getDeclaredMethod("onReload");
                onReload();
            } catch (NoSuchMethodException ignored) {

            }
            wasReloaded = false;
        } else {
            try {
                getClass().getDeclaredMethod("onStart");
                onStart();
            } catch (NoSuchMethodException ignored) {

            }
        }
        ResourceUtils.saveFiles();
        ListenerRegister.registerListeners();
    }

    @Override
    public void onDisable() {
        if (this.isEnabled()) {
            wasReloaded = true;
        }

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

    protected void onReload() {

    }

    protected void onStop() {

    }
}