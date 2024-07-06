package dev.vansen.utility.component;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class Deserializer {

    public static Component deserialize(@NotNull String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }
}