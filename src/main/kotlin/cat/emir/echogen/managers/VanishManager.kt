package cat.emir.echogen.managers

import cat.emir.echogen.Echogen
import cat.emir.echogen.toComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class VanishManager(val plugin: Echogen) : Listener {
    val vanishedPlayers = mutableListOf<UUID>()

    fun load() {
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            for (uuid in vanishedPlayers) {
                plugin.server.getPlayer(uuid)?.sendActionBar("<green>You are currently vanished.</green>".toComponent())
            }
        }, 0, 1)
    }

    fun isVanished(player: Player): Boolean {
        return vanishedPlayers.contains(player.uniqueId)
    }

    fun vanish(player: Player) {
        vanishedPlayers.add(player.uniqueId)

        for (oPlayer in plugin.server.onlinePlayers) {
            if (!oPlayer.hasPermission("echogen.vanish"))
                oPlayer.hidePlayer(plugin, player)
        }
    }

    fun unVanish(player: Player) {
        vanishedPlayers.remove(player.uniqueId)

        for (oPlayer in plugin.server.onlinePlayers) {
            oPlayer.showPlayer(plugin, player)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPermission("echogen.vanish")) return

        for (uuid in vanishedPlayers) {
            player.hidePlayer(plugin, plugin.server.getPlayer(uuid)!!)
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        vanishedPlayers.remove(player.uniqueId)
    }
}
