package cat.emir.echogen.events

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import cat.emir.echogen.Echogen
import cat.emir.echogen.task.RamBarTask
import cat.emir.echogen.task.TPSBarTask
import cat.emir.echogen.toComponent

class PlayerJoinListener(val plugin: Echogen) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        
        if (RamBarTask.instance(plugin).hasPlayer(player.uniqueId)) {
            RamBarTask.instance(plugin).addPlayer(player)
        }
        if (TPSBarTask.instance(plugin).hasPlayer(player.uniqueId)) {
            TPSBarTask.instance(plugin).addPlayer(player)
        }

        var joinMessage = plugin.config.rootNode.node("chat", "messages", "join").string ?: return

        if (joinMessage.isEmpty()) {
            event.joinMessage(null)
            return
        }

        if (plugin.isPAPIEnabled) {
            joinMessage = PlaceholderAPI.setPlaceholders(player, joinMessage)
        }

        event.joinMessage(joinMessage.toComponent(
                Placeholder.parsed("name", player.name)
        ))
    }
}
