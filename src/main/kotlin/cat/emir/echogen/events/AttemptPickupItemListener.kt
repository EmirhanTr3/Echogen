package cat.emir.echogen.events

import cat.emir.echogen.Echogen
import cat.emir.echolib.event.EchoEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerAttemptPickupItemEvent

class AttemptPickupItemListener(val plugin: Echogen) : EchoEvent(plugin) {

    @EventHandler
    fun onPickupItem(event: PlayerAttemptPickupItemEvent) {
        val player = event.player

        if (plugin.vanishManager.isVanished(player)) {
            event.isCancelled = true
        }
    }
}