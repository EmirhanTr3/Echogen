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

class FlyCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("fly") {
            requires { it.sender.hasPermission("echogen.fly") }
            executes(::fly)
            argument("players", ArgumentTypes.players()) {
                requires { it.sender.hasPermission("echogen.fly.others") }
                executes(::flyOther)
            }
        }
    }

    fun fly(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val allowFlight = player.allowFlight

        player.allowFlight = !allowFlight

        if (!allowFlight)
            player.sendLangMessage("fly.self.enabled")
        else
            player.sendLangMessage("fly.self.disabled")

        return 1
    }

    fun flyOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            val allowFlight = target.allowFlight

            target.allowFlight = !allowFlight

            val data = listOf("player" to target.name)

            if (!allowFlight) {
                sender.sendLangMessage("fly.other.enabled.executor", data)
                target.sendLangMessage("fly.other.enabled.target")
            } else {
                sender.sendLangMessage("fly.other.disabled.executor", data)
                target.sendLangMessage("fly.other.disabled.target", data)
            }
        }

        return 1
    }
}
