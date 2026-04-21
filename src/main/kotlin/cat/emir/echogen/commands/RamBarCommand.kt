package cat.emir.echogen.commands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.task.RamBarTask;
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class RamBarCommand : PluginCommand() {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("rambar") {
            requires { it.sender.hasPermission("echogen.rambar") }
            executes(::execute)
        }
    }

    fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val result = RamBarTask.instance(plugin).togglePlayer(player);

        player.sendRichMessage("<green>Rambar toggled <onoff> for <target>",
            Placeholder.component("onoff", Component.text(if (result) "on" else "off")
                .color(if (result) NamedTextColor.GREEN else NamedTextColor.RED)),
            Placeholder.parsed("target", player.name));

        return 1;
    }
}
