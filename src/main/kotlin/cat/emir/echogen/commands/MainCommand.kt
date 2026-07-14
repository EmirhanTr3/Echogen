package cat.emir.echogen.commands

import cat.emir.echogen.Echogen
import com.mojang.brigadier.context.CommandContext

import io.papermc.paper.command.brigadier.CommandSourceStack
import cat.emir.echolib.command.PluginCommand
import cat.emir.echolib.sendLangMessage
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class MainCommand(plugin: Echogen) : PluginCommand<Echogen>(plugin) {

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

        sender.sendLangMessage("reload.start")
        val time = plugin.reload()
        sender.sendLangMessage("reload.end", listOf("ms" to time.toString()))

        return 1
    }
}
