package dev.vansen.utility.commands;

import dev.vansen.utility.PluginHolder;
import dev.vansen.utility.annotations.Completes;
import dev.vansen.utility.annotations.Register;
import dev.vansen.utility.plugin.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class CommandsManager {

    private final CommandMap commandMap;
    private final Map<String, Command> commandsMap = new HashMap<>();

    public CommandsManager() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            this.commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            PluginHolder.getPluginInstance().getLogger().log(Level.SEVERE, "Could not retrieve command map", e);
            throw new RuntimeException(e);
        }
        registerCommands();
        registerTabCompleters();
    }

    private void registerCommands() {
        Reflections reflections = new Reflections("");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Register.class)) {
            if (CommandExecutor.class.isAssignableFrom(clazz)) {
                Register register = clazz.getAnnotation(Register.class);
                try {
                    CommandExecutor executor = (CommandExecutor) clazz.getDeclaredConstructor().newInstance();
                    CustomCommand command = new CustomCommand(register.name(), executor);
                    command.setDescription(register.description());
                    command.setAliases(List.of(register.aliases()));
                    command.setPermission(register.permission());

                    commandsMap.put(register.name(), command);

                    commandMap.register(PluginHolder.getPluginInstance().getDescription().getName(), command);
                } catch (Exception e) {
                    PluginUtils.getLogger().log(Level.SEVERE, "Could not register command: " + register.name(), e);
                }
            }
        }
    }

    private void registerTabCompleters() {
        Reflections reflections = new Reflections("");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Completes.class)) {
            if (TabCompleter.class.isAssignableFrom(clazz)) {
                Completes completes = clazz.getAnnotation(Completes.class);
                try {
                    TabCompleter completer = (TabCompleter) clazz.getDeclaredConstructor().newInstance();
                    Command command = commandsMap.get(completes.value());
                    if (command != null) {
                        ((CustomCommand) command).setTabCompleter(completer);
                    } else {
                        PluginHolder.getPluginInstance().getLogger().log(Level.WARNING, "Command " + completes.value() + " not found for tab completion");
                    }
                } catch (Exception e) {
                    PluginUtils.getLogger().log(Level.SEVERE, "Could not register tab completer for command: " + completes.value(), e);
                }
            }
        }
    }

    private static class CustomCommand extends BukkitCommand {
        private final CommandExecutor executor;
        private TabCompleter tabCompleter;

        public CustomCommand(@NotNull String name, @NotNull CommandExecutor executor) {
            super(name);
            this.executor = executor;
        }

        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
            return executor.onCommand(sender, this, commandLabel, args);
        }

        @Override
        public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
            if (tabCompleter != null) {
                return tabCompleter.onTabComplete(sender, this, alias, args);
            }
            return super.tabComplete(sender, alias, args);
        }

        public void setTabCompleter(TabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
        }
    }
}