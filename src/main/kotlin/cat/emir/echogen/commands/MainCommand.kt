package cat.emir.echogen.commands

import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import cat.emir.echogen.commandlib.PluginCommand
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class MainCommand : PluginCommand() {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("echogen") {
            requires { it.sender.hasPermission("echogen.admin") }
            subcommand("reload") {
                executes(::reload)
            }
        }
    }

    fun reload(ctx: CommandContext<CommandSourceStack>): Int {
        val sender = ctx.source.sender
        val startedAt = System.currentTimeMillis()

        sender.sendRichMessage("<aqua>Reloading Echogen configuration...")

        plugin.reloadConfig()

        sender.sendRichMessage(
            "<aqua>Reloaded Echogen configuration in <u><ms></u>ms.",
            Placeholder.unparsed("ms", (System.currentTimeMillis() - startedAt).toString()))

        return 1
    }
}
