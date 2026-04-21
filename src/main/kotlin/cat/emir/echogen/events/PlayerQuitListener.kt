package cat.emir.echogen.events

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import cat.emir.echogen.Echogen
import cat.emir.echogen.toComponent

class PlayerQuitListener(val plugin: Echogen) : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        var quitMessage = plugin.config.rootNode.node("chat", "messages", "leave").string ?: return

        if (quitMessage.isEmpty()) {
            event.quitMessage(null)
            return
        }

        val player = event.player

        if (plugin.isPAPIEnabled) {
            quitMessage = PlaceholderAPI.setPlaceholders(player, quitMessage)
        }

        event.quitMessage(quitMessage.toComponent(
                Placeholder.parsed("name", player.name)
        ))
    }
}
