package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class VanishCommand : PluginCommand() {

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
            player.sendRichMessage("<aqua>You have <green>enabled</green> vanish.</aqua>")
        } else {
            plugin.vanishManager.unVanish(player)
            player.sendRichMessage("<aqua>You have <red>disabled</red> vanish.</aqua>")
        }

        return 1
    }

    fun vanishOther(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val target = ctx.getPlayer("player") ?: return 1
        val vanished = plugin.vanishManager.isVanished(target)

        val playerTag = Placeholder.unparsed("player", target.name)

        if (!vanished) {
            plugin.vanishManager.vanish(target)
            sender.sendRichMessage("<aqua>You have <green>enabled</green> vanish for <player>.</aqua>", playerTag)
            target.sendRichMessage("<aqua>Your vanish has been <green>enabled</green>.</aqua>")
        } else {
            plugin.vanishManager.unVanish(target)
            sender.sendRichMessage("<aqua>You have <red>disabled</red> vanish for <player>.</aqua>", playerTag)
            target.sendRichMessage("<aqua>Your vanish has been <red>disabled</red>.</aqua>")
        }

        return 1
    }
}
