package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.commandlib.getPlayers
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class FlyCommand : PluginCommand() {

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
            player.sendRichMessage("<aqua>You have <green>enabled</green> flight mode.</aqua>")
        else
            player.sendRichMessage("<aqua>You have <red>disabled</red> flight mode.</aqua>")

        return 1
    }

    fun flyOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1

        for (target in targets) {
            val allowFlight = target.allowFlight

            target.allowFlight = !allowFlight

            val playerTag = Placeholder.unparsed("player", target.name)
            if (!allowFlight) {
                sender.sendRichMessage("<aqua>You have <green>enabled</green> flight mode for <player>.</aqua>", playerTag)
                target.sendRichMessage("<aqua>Your flight mode was <green>enabled</green>.</aqua>")
            } else {
                sender.sendRichMessage("<aqua>You have <red>disabled</red> flight mode for <player>.</aqua>", playerTag)
                target.sendRichMessage("<aqua>Your flight mode was <red>disabled</red>.</aqua>")
            }
        }

        return 1
    }
}
