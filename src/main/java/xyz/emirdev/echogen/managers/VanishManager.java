package xyz.emirdev.echogen.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.Utils;

public class VanishManager implements Listener {
    private List<Player> vanishedPlayers = new ArrayList<>();

    public VanishManager() {
        this.run();
    }

    public void run() {
        Bukkit.getScheduler().runTaskTimer(Echogen.get(), () -> {
            for (Player player : vanishedPlayers) {
                player.sendActionBar(Utils.formatMessage("<green>You are currently vanished.</green>"));
            }
        }, 0, 1);
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    public void vanish(Player player) {
        vanishedPlayers.add(player);

        for (Player oPlayer : Bukkit.getOnlinePlayers()) {
            oPlayer.hidePlayer(Echogen.get(), player);
        }
    }

    public void unVanish(Player player) {
        vanishedPlayers.remove(player);

        for (Player oPlayer : Bukkit.getOnlinePlayers()) {
            oPlayer.showPlayer(Echogen.get(), player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        for (Player vPlayer : vanishedPlayers) {
            player.hidePlayer(Echogen.get(), vPlayer);
        }
    }
}
