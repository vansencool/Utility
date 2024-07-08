package dev.vansen.utility.message;

import dev.vansen.utility.component.Deserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class TitleSender {

    public static void sendTitle(@NotNull Player player, @NotNull String titleMessage, @NotNull String subtitleMessage, int fadeIn, int stay, int fadeOut) {
        Component titleComponent = Deserializer.deserialize(titleMessage);
        Component subtitleComponent = Deserializer.deserialize(subtitleMessage);

        Title.Times times = Title.Times.times(
                Duration.ofSeconds(fadeIn),
                Duration.ofSeconds(stay),
                Duration.ofSeconds(fadeOut)
        );

        Title title = Title.title(
                titleComponent,
                subtitleComponent,
                times
        );

        player.showTitle(title);
    }

    public static class Builder {
        private Player player;
        private String titleMessage;
        private String subtitleMessage;
        private int fadeIn;
        private int stay;
        private int fadeOut;

        public Builder player(@NotNull Player player) {
            this.player = player;
            return this;
        }

        public Builder titleMessage(@NotNull String titleMessage) {
            this.titleMessage = titleMessage;
            return this;
        }

        public Builder subtitleMessage(@NotNull String subtitleMessage) {
            this.subtitleMessage = subtitleMessage;
            return this;
        }

        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        public Builder stay(int stay) {
            this.stay = stay;
            return this;
        }

        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public void send() {
            TitleSender.sendTitle(player, titleMessage, subtitleMessage, fadeIn, stay, fadeOut);
        }
    }
}