package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import cat.emir.echogen.getDuration
import cat.emir.echolib.command.PluginCommand
import java.time.Duration
import cat.emir.echogen.utils.TimeUtils
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack

class ChatCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {
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

        plugin.server.sendLangMessage("chat.clear")

        return 1
    }

    fun mute(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender

        if (!isChatMuted) {
            isChatMuted = true
            plugin.server.sendLangMessage("chat.mute", listOf("player" to sender.name))
        } else {
            isChatMuted = false
            plugin.server.sendLangMessage("chat.unmute", listOf("player" to sender.name))
        }

        return 1
    }

    fun slowmode(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val duration = ctx.getDuration("duration") ?: return 1

        slowmode = duration

        plugin.server.sendLangMessage("chat.slowmode.set", listOf(
            "player" to sender.name,
            "duration" to TimeUtils.parseDurationToString(duration)!!,
        ))

        return 1
    }

    fun slowmodeClear(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender

        slowmode = null

        plugin.server.sendLangMessage("chat.slowmode.clear", listOf("player" to sender.name))

        return 1
    }
}
