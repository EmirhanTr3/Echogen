package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayers
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SmiteCommand : PluginCommand() {
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

            sender.sendRichMessage(
                "<aqua>You have smitten <player>!</aqua>",
                Placeholder.unparsed("player", target.name))
        }

        return 1
    }
}
