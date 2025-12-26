package xyz.emirdev.echogen.events;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.utils.Utils;

public class PlayerQuitListener implements Listener {

    @EventHandler()
    public void onQuit(PlayerQuitEvent event) {
        String quitMessage = Echogen.get().getPluginConfig().getRootNode().node("chat", "messages", "leave").getString();
        if (quitMessage == null) return;

        if (quitMessage.isEmpty()) {
            event.quitMessage(null);
            return;
        }

        Player player = event.getPlayer();

        Utils.formatMessage(quitMessage,
                Placeholder.parsed("name", player.getName())
        );
    }
}
