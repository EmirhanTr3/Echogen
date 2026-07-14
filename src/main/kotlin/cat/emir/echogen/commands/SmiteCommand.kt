package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayers
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SmiteCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {
    override val aliases = setOf("thor")

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("smite") {
            requires { it.sender.hasPermission("echogen.smite") }
            argument("players", ArgumentTypes.players()) {
                executes(::smite)
            }
        }
    }

    fun smite(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            target.world.strikeLightningEffect(target.location)
            sender.sendLangMessage("smite.executor", listOf("player" to target.name))
        }

        return 1
    }
}
