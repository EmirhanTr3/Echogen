package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayer
import cat.emir.echolib.extensions.toComponent
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
            var format = plugin.config.rootNode.node("chat", "ownerchat-format").string ?: "<name>: <message>"
            val component = MiniMessage.miniMessage().deserialize(message)

            if (plugin.isPAPIEnabled)
                format = PlaceholderAPI.setPlaceholders(sender as? Player, format)

            plugin.server.onlinePlayers
                .filter { it.hasPermission("echogen.ownerchat") }
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
            requires { it.sender.hasPermission("echogen.ownerchat") }
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
            player.sendMessage("<aqua>You have <red>disabled</red> owner chat.</aqua>".toComponent())
        } else {
            toggledPlayers.add(player.uniqueId)
            player.sendMessage("<aqua>You have <green>enabled</green> owner chat.</aqua>".toComponent())
        }

        return 1
    }


    fun message(ctx: CommandContext<CommandSourceStack>): Int {
        val message = StringArgumentType.getString(ctx, "message")

        sendOwnerChatMessage(ctx.source.sender, message)

        return 1
    }
}