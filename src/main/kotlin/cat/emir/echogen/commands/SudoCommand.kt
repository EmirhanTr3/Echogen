package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayers
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SudoCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("sudo") {
            requires { it.sender.hasPermission("echogen.sudo") }
            argument("players", ArgumentTypes.players()) {
                argument("message", StringArgumentType.greedyString()) {
                    executes(::sudo)
                }
            }
        }
    }

    fun sudo(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val message = StringArgumentType.getString(ctx, "message")

        for (target in targets) {
            target.chat(message)

            sender.sendLangMessage("sudo.executor", listOf(
                "player" to target.name,
                "messsage" to message
            ))
        }

        return 1
    }
}
