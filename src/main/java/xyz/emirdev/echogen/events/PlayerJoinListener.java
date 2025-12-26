package xyz.emirdev.echogen.events;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.task.RamBarTask;
import xyz.emirdev.echogen.task.TPSBarTask;
import xyz.emirdev.echogen.utils.Utils;

public class PlayerJoinListener implements Listener {

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        if (RamBarTask.instance().hasPlayer(event.getPlayer().getUniqueId())) {
            RamBarTask.instance().addPlayer(event.getPlayer());
        }
        if (TPSBarTask.instance().hasPlayer(event.getPlayer().getUniqueId())) {
            TPSBarTask.instance().addPlayer(event.getPlayer());
        }

        String joinMessage = Echogen.get().getPluginConfig().getRootNode().node("chat", "messages", "join").getString();
        if (joinMessage == null) return;

        if (joinMessage.isEmpty()) {
            event.joinMessage(null);
            return;
        }

        Player player = event.getPlayer();

        Utils.formatMessage(joinMessage,
                Placeholder.parsed("name", player.getName())
        );
    }
}
