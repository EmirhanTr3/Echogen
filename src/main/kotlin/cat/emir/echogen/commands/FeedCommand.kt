package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayer
import cat.emir.echolib.command.getPlayers
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class FeedCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {
    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("feed") {
            requires { it.sender.hasPermission("echogen.feed") }
            executes(::feed)
            argument("players", ArgumentTypes.players()) {
                requires { it.sender.hasPermission("echogen.feed.others") }
                executes(::feedOthers)
            }
        }
    }

    fun feed(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1

        player.foodLevel = 20

        player.sendLangMessage("feed.target")

        return 1
    }

    fun feedOthers(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            target.foodLevel = 20

            sender.sendLangMessage("feed.executor", listOf("player" to target.name))
            target.sendLangMessage("feed.target")
        }

        return 1
    }
}
