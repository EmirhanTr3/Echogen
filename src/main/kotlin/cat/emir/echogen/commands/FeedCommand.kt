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

class FeedCommand : PluginCommand() {
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

        player.sendRichMessage("<aqua>You have been fed.</aqua>")

        return 1
    }

    fun feedOthers(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            target.foodLevel = 20

            sender.sendRichMessage("<aqua>You have fed <player>.</aqua>", Placeholder.unparsed("player", target.name))
            target.sendRichMessage("<aqua>You have been fed.</aqua>")
        }

        return 1
    }
}
