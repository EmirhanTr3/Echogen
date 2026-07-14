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
import org.bukkit.attribute.Attribute

class HealCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {
    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("heal") {
            requires { it.sender.hasPermission("echogen.heal") }
            executes(::heal)
            argument("players", ArgumentTypes.players()) {
                requires { it.sender.hasPermission("echogen.heal.others") }
                executes(::healOthers)
            }
        }
    }

    fun heal(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1

        player.health = player.getAttribute(Attribute.MAX_HEALTH)!!.value
        if (plugin.config.rootNode.node("other", "heal-also-feeds").boolean)
            player.foodLevel = 20

        player.sendLangMessage("heal.target")

        return 1
    }

    fun healOthers(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            target.health = target.getAttribute(Attribute.MAX_HEALTH)!!.value
            if (plugin.config.rootNode.node("other", "heal-also-feeds").boolean)
                target.foodLevel = 20

            sender.sendLangMessage("heal.executor", listOf("player" to target.name))
            target.sendLangMessage("heal.target")
        }

        return 1
    }
}
