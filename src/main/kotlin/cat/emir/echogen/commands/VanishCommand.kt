package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayer
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class VanishCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("vanish") {
            requires { it.sender.hasPermission("echogen.vanish") }
            executes(::vanish)
            argument("player", ArgumentTypes.player()) {
                requires { it.sender.hasPermission("echogen.vanish.others") }
                executes(::vanishOther)
            }
        }
    }

    fun vanish(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val vanished = plugin.vanishManager.isVanished(player)

        if (!vanished) {
            plugin.vanishManager.vanish(player)
            player.sendLangMessage("vanish.self.enabled")
        } else {
            plugin.vanishManager.unVanish(player)
            player.sendLangMessage("vanish.self.disabled")
        }

        return 1
    }

    fun vanishOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val target = ctx.getPlayer("player") ?: return 1
        val vanished = plugin.vanishManager.isVanished(target)

        val data = listOf("player" to target.name)

        if (!vanished) {
            plugin.vanishManager.vanish(target)
            sender.sendLangMessage("vanish.other.enabled.executor", data)
            target.sendLangMessage("vanish.other.enabled.target")
        } else {
            plugin.vanishManager.unVanish(target)
            sender.sendLangMessage("vanish.other.disabled.executor", data)
            target.sendLangMessage("vanish.other.disabled.target")
        }

        return 1
    }
}
