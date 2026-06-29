package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.command.getPlayers
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class SudoCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("sudo") {
            requires { it.sender.hasPermission("echogen.sudo") }
            argument("players", ArgumentTypes.players()) {
                argument("message", StringArgumentType.greedyString()) {
                    executes(::sudo)
                }
            }
        }
    }

    fun sudo(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val targets = ctx.getPlayers("players") ?: return 1
        val message = StringArgumentType.getString(ctx, "message")

        for (target in targets) {
            target.chat(message)

            sender.sendRichMessage(
                "<aqua>You have made <dark_aqua><player></dark_aqua> send <dark_aqua><message></dark_aqua></aqua>",
                Placeholder.unparsed("player", target.name),
                Placeholder.unparsed("message", message))
        }

        return 1
    }
}
