package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayer
import cat.emir.echolib.extensions.toComponent
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class OwnerChatCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

    companion object {
        val toggledPlayers = mutableSetOf<UUID>()

        fun sendOwnerChatMessage(sender: CommandSender, message: Component) {
            sendOwnerChatMessage(sender, PlainTextComponentSerializer.plainText().serialize(message))
        }

        fun sendOwnerChatMessage(sender: CommandSender, message: String) {
            val plugin = Echogen.instance
            var format = plugin.config.rootNode.node("chat", "ownerchat", "format").string ?: "<name>: <message>"
            val component = MiniMessage.miniMessage().deserialize(message)

            if (plugin.isPAPIEnabled)
                format = PlaceholderAPI.setPlaceholders(sender as? Player, format)

            plugin.server.onlinePlayers
                .filter {
                    if (plugin.config.rootNode.node("chat", "ownerchat", "uuid-only").boolean) {
                        val list = plugin.config.rootNode.node("chat", "ownerchat", "uuids")
                            .getList(String::class.java) ?: emptyList<String>()
                        list.contains(it.uniqueId.toString())
                    } else {
                        it.hasPermission("echogen.ownerchat")
                    }
                }
                .forEach {
                    it.sendMessage(format.toComponent(
                        Placeholder.parsed("prefix",
                            if (sender is Player) plugin.luckPermsUtils.getPrefix(sender)
                            else ""
                        ),
                        Placeholder.parsed("suffix",
                            if (sender is Player) plugin.luckPermsUtils.getSuffix(sender)
                            else ""
                        ),
                        Placeholder.parsed("name", sender.name),
                        Placeholder.component("message", component)
                    ))
                }
        }
    }

    override val aliases = setOf("oc")

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("ownerchat") {
            requires {
                if (plugin.config.rootNode.node("chat", "ownerchat", "uuid-only").boolean && it.sender is Player) {
                    val list = plugin.config.rootNode.node("chat", "ownerchat", "uuids")
                        .getList(String::class.java) ?: emptyList<String>()
                    list.contains((it.sender as Player).uniqueId.toString())
                } else {
                    it.sender.hasPermission("echogen.ownerchat")
                }
            }
            executes(::toggle)
            argument("message", StringArgumentType.greedyString()) {
                executes(::message)
            }
        }
    }

    fun toggle(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return -1

        if (toggledPlayers.contains(player.uniqueId)) {
            toggledPlayers.remove(player.uniqueId)
            player.sendLangMessage("ownerchat.disabled")
        } else {
            toggledPlayers.add(player.uniqueId)
            player.sendLangMessage("ownerchat.enabled")
        }

        return 1
    }


    fun message(ctx: CommandContext<CommandSourceStack>): Int {
        val message = StringArgumentType.getString(ctx, "message")

        sendOwnerChatMessage(ctx.source.sender, message)

        return 1
    }
}