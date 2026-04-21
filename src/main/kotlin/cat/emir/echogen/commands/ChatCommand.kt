package cat.emir.echogen.commands

import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getDuration
import java.time.Duration

import com.mojang.brigadier.arguments.StringArgumentType

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.utils.TimeUtils
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack

class ChatCommand : PluginCommand() {
    companion object {
        var isChatMuted = false
        var slowmode: Duration? = null
    }

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("chat") {
            requires { it.sender.hasPermission("echogen.chat") }
            subcommand("clear") {
                requires { it.sender.hasPermission("echogen.chat.clear") }
                executes(::clear)
            }
            subcommand("mute") {
                requires { it.sender.hasPermission("echogen.chat.mute") }
                executes(::mute)
            }
            subcommand("slowmode") {
                requires { it.sender.hasPermission("echogen.chat.slowmode") }
                subcommand("clear") {
                    executes(::slowmodeClear)
                }
                argument("duration", StringArgumentType.word()) {
                    executes(::slowmode)
                }
            }
        }
    }

    fun clear(ctx: CommandContext<CommandSourceStack>): Int {
        for (player in plugin.server.onlinePlayers) {
            if (player.hasPermission("echogen.chat.clear.bypass"))
                continue

            for (i in 1..1000) {
                player.sendMessage(" ".repeat(i % 60))
            }
        }

        plugin.server.sendRichMessage("<aqua>Chat has been cleared.</aqua>")

        return 1
    }

    fun mute(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender

        val playerTag = Placeholder.unparsed("player", sender.name)

        if (!isChatMuted) {
            isChatMuted = true
            plugin.server.sendRichMessage("<aqua>Chat has been <red>muted</red> by <player>.</aqua>", playerTag)
        } else {
            isChatMuted = false
            plugin.server.sendRichMessage("<aqua>Chat has been <green>unmuted</green> by <player>.</aqua>", playerTag)
        }

        return 1
    }

    fun slowmode(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val duration = ctx.getDuration("duration") ?: return 1

        slowmode = duration

        plugin.server.sendRichMessage(
            "<aqua>Chat slowmode has been set to <dark_aqua><duration></dark_aqua> by <player>.</aqua>",
            Placeholder.unparsed("duration", TimeUtils.parseDurationToString(duration)!!),
            Placeholder.unparsed("player", sender.name))

        return 1
    }

    fun slowmodeClear(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender

        slowmode = null

        plugin.server.sendRichMessage(
            "<aqua>Chat slowmode has been <dark_aqua>disabled</dark_aqua> by <player>.</aqua>",
            Placeholder.unparsed("player", sender.name))

        return 1
    }
}
