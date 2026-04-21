package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.commandlib.getPlayers
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.attribute.Attribute

class HealCommand : PluginCommand() {
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

        player.sendRichMessage("<aqua>You have been healed.</aqua>")

        return 1
    }

    fun healOthers(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            target.health = target.getAttribute(Attribute.MAX_HEALTH)!!.value
            if (plugin.config.rootNode.node("other", "heal-also-feeds").boolean)
                target.foodLevel = 20

            sender.sendRichMessage("<aqua>You have healed <player>.</aqua>", Placeholder.unparsed("player", target.name))
            target.sendRichMessage("<aqua>You have been healed.</aqua>")
        }

        return 1
    }
}
