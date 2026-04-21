package cat.emir.echogen.commands

import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack

import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.inventory.InventoryHolder

class InvseeCommand : PluginCommand() {
    override fun meetsRequirements(): Boolean {
        plugin.logger.info("Disabled invsee command due to openinv being present.")
        return !plugin.isOpenInvEnabled
    }

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("invsee") {
            requires { it.sender.hasPermission("echogen.invsee") }
            argument("player", ArgumentTypes.player()) {
                executes(::invsee)
            }
        }
    }

    fun invsee(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val target = ctx.getPlayer("player") ?: return 1

        player.openInventory(target.inventory)

        return 1
    }
}
