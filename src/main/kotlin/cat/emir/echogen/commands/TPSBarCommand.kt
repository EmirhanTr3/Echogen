package cat.emir.echogen.commands;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import cat.emir.echogen.commandlib.PluginCommand
import cat.emir.echogen.commandlib.getPlayer
import cat.emir.echogen.task.TPSBarTask;
import com.mojang.brigadier.builder.LiteralArgumentBuilder

class TPSBarCommand : PluginCommand() {

    override fun getCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return command("tpsbar") {
            requires { it.sender.hasPermission("echogen.tpsbar") }
            executes(::execute)
        }
    }

    fun execute(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.getPlayer() ?: return 1
        val result = TPSBarTask.instance(plugin).togglePlayer(player);

        player.sendRichMessage("<green>Tpsbar toggled <onoff> for <target>",
            Placeholder.component("onoff", Component.text(if (result) "on" else "off")
                .color(if (result) NamedTextColor.GREEN else NamedTextColor.RED)),
            Placeholder.parsed("target", player.name));

        return 1;
    }
}
