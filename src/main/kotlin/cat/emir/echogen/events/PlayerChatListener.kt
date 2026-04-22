package cat.emir.echogen.events

import java.time.Duration
import java.util.UUID

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import cat.emir.echogen.Echogen
import cat.emir.echogen.commands.ChatCommand
import cat.emir.echogen.managers.FilterManager
import cat.emir.echogen.toComponent
import cat.emir.echogen.utils.TimeUtils

class PlayerChatListener(val plugin: Echogen) : Listener {
    val slowmodePlayers = mutableMapOf<UUID, Long>()

    val noFormatMiniMessage = MiniMessage.builder()
        .tags(TagResolver.resolver(
            StandardTags.color(),
            StandardTags.decorations(),
            StandardTags.rainbow(),
            StandardTags.gradient(),
            StandardTags.pride(),
            StandardTags.sequentialHead(),
            StandardTags.sprite(),
            StandardTags.shadowColor()
        ))
        .build()

    fun getChatFormat(player: Player, message: Component, prefix: String?): Component {
        val rootNode = plugin.config.rootNode

        var component = if (player.hasPermission("echogen.chat.component")) {
            if (player.hasPermission("echogen.chat.component.format"))
                MiniMessage.miniMessage().deserialize(PlainTextComponentSerializer.plainText().serialize(message))
            else
                noFormatMiniMessage.deserialize(PlainTextComponentSerializer.plainText().serialize(message))
        } else message

        val item = player.inventory.itemInMainHand
        if (!item.type.isAir && rootNode.node("chat", "item", "enabled").boolean) {
            val requiresPermission = rootNode.node("chat", "item", "permission").boolean
            if (!requiresPermission || player.hasPermission("echogen.chat.item")) {
                component = component.replaceText { it
                    .matchLiteral("[item]")
                    .replacement(item.displayName().hoverEvent(item.asHoverEvent()))
                    .build()
                }
            }
        }

        var format = rootNode.node("chat", "format").string ?: return message

        if (plugin.isPAPIEnabled)
            format = PlaceholderAPI.setPlaceholders(player, format)

        return format.toComponent(
                Placeholder.parsed("prefix", prefix ?: plugin.luckPermsUtils.getPrefix(player)),
                Placeholder.parsed("suffix", plugin.luckPermsUtils.getSuffix(player)),
                Placeholder.parsed("name", player.name),
                Placeholder.component("message", component)
        )
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun chatEvent(event: AsyncChatEvent) {
        val player = event.getPlayer()
        val rootNode = plugin.config.rootNode
        if (!rootNode.node("chat", "enabled").boolean) return

        val prefix = plugin.prefixManager.getPlayerPrefixString(player)
        val component = getChatFormat(player, event.message(), prefix)

        event.renderer { _, _, _, _ -> component }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun chatCmdEvent(event: AsyncChatEvent) {
        val player = event.player
        val uuid = player.uniqueId

        if (ChatCommand.isChatMuted && !player.hasPermission("echogen.chat.mute.bypass")) {
            player.sendRichMessage("<red>Chat is currently muted.</red>")
            event.isCancelled = true
            return
        }

        if (ChatCommand.slowmode != null && !player.hasPermission("echogen.chat.slowmode.bypass")) {
            if (!slowmodePlayers.containsKey(uuid)
                    || System.currentTimeMillis() > slowmodePlayers[uuid]!! + ChatCommand.slowmode!!.toMillis()) {
                slowmodePlayers[uuid] = System.currentTimeMillis()
            } else {
                val expiresAt = slowmodePlayers[uuid]?.plus(ChatCommand.slowmode!!.toMillis())
                val timeLeft = Duration.ofMillis(expiresAt?.minus(System.currentTimeMillis())!!)

                player.sendRichMessage(
                    "<red>You cannot send a message for <dark_red><duration></dark_red>.</red>",
                    Placeholder.unparsed("duration", TimeUtils.parseDurationToString(timeLeft)!!))
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun chatFilterEvent(event: AsyncChatEvent) {
        val player = event.player
        if (player.hasPermission("echogen.chat.filter.bypass")) return

        val filters = plugin.filterManager.filters
        if (filters.isEmpty()) return

        var message = PlainTextComponentSerializer.plainText().serialize(event.message())

        for (filter in filters) {
            val matcher = filter.pattern.matcher(message)

            while (matcher.find()) {
                when (filter.type) {
                    FilterManager.FilterType.CENSOR ->
                        message = message.replace(
                            matcher.group(0),
                            matcher.group(0).replace(Regex("."), "*"))
                    FilterManager.FilterType.BLOCK_MESSAGE -> {
                        event.isCancelled = true
                        player.sendRichMessage("<red>Your message has been filtered.</red>")
                        return
                    }
                    FilterManager.FilterType.COMMAND -> {
                        event.isCancelled = true
                        player.sendRichMessage("<red>Your message has been filtered.</red>")
                        plugin.server.scheduler.runTask(plugin, Runnable {
                            plugin.server.dispatchCommand(plugin.server.consoleSender,
                                filter.command!!
                                    .replace("<player>", player.name)
                                    .replace("<uuid>", player.uniqueId.toString()))
                        })
                        return
                    }
                }
            }
        }

        event.message(PlainTextComponentSerializer.plainText().deserialize(message))
    }
}
