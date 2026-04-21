package cat.emir.echogen.commands

import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack

import io.papermc.paper.command.brigadier.argument.ArgumentTypes

class EnderChestCommand : PluginCommand() {
    override val aliases = setOf("ec")

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("enderchest") {
            requires { it.sender.hasPermission("echogen.enderchest") }
            executes(::enderchest)
            argument("player", ArgumentTypes.player()) {
                requires { it.sender.hasPermission("echogen.enderchest.others") }
                executes(::enderchestOther)
            }
        }
    }

    fun enderchest(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1

        player.openInventory(player.enderChest)

        return 1
    }

    fun enderchestOther(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val target = ctx.getPlayer("player") ?: return 1

        player.openInventory(target.enderChest)

        return 1
    }
}
