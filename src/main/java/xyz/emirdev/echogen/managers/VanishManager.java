package xyz.emirdev.echogen.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.utils.Utils;

public class VanishManager implements Listener {
    private List<UUID> vanishedPlayers = new ArrayList<>();

    public VanishManager() {
        this.run();
    }

    public void run() {
        Bukkit.getScheduler().runTaskTimer(Echogen.get(), () -> {
            for (UUID uuid : vanishedPlayers) {
                Bukkit.getPlayer(uuid).sendActionBar(Utils.formatMessage("<green>You are currently vanished.</green>"));
            }
        }, 0, 1);
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public void vanish(Player player) {
        vanishedPlayers.add(player.getUniqueId());

        for (Player oPlayer : Bukkit.getOnlinePlayers()) {
            if (!oPlayer.hasPermission("echogen.vanish"))
                oPlayer.hidePlayer(Echogen.get(), player);
        }
    }

    public void unVanish(Player player) {
        vanishedPlayers.remove(player.getUniqueId());

        for (Player oPlayer : Bukkit.getOnlinePlayers()) {
            oPlayer.showPlayer(Echogen.get(), player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("echogen.vanish"))
            return;

        for (UUID uuid : vanishedPlayers) {
            player.hidePlayer(Echogen.get(), Bukkit.getPlayer(uuid));
        }
    }
}
