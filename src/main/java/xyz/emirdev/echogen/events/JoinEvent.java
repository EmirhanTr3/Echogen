package xyz.emirdev.echogen.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.emirdev.echogen.task.RamBarTask;
import xyz.emirdev.echogen.task.TPSBarTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JoinEvent implements Listener {

    @EventHandler()
    public void onJoin(PlayerJoinEvent event) {
        if (RamBarTask.instance().hasPlayer(event.getPlayer().getUniqueId())) {
            RamBarTask.instance().addPlayer(event.getPlayer());
        }
        if (TPSBarTask.instance().hasPlayer(event.getPlayer().getUniqueId())) {
            TPSBarTask.instance().addPlayer(event.getPlayer());
        }
    }
}
