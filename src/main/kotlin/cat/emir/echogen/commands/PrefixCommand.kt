package cat.emir.echogen.commands

import io.papermc.paper.command.brigadier.CommandSourceStack
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.guis.PrefixGUI
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext

class PrefixCommand : PluginCommand() {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("prefix") {
            requires {
                it.sender.hasPermission("echogen.chat.prefix") &&
                plugin.config.rootNode.node("chat", "prefix", "enabled").boolean
            }
            executes(::execute)
        }
    }

    fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1

        PrefixGUI(plugin, player).openGUI()

        return 1
    }
}
