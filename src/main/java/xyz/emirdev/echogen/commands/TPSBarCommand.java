package xyz.emirdev.echogen.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xyz.emirdev.echogen.PluginCommand;
import xyz.emirdev.echogen.task.TPSBarTask;

public class TPSBarCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("tpsbar")
                .requires(hasPermission("echogen.tpsbar"))
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        Player player = getContextPlayer(ctx);
        if (player == null) return 1;

        boolean result = TPSBarTask.instance().togglePlayer(player);

        player.sendRichMessage("<green>Tpsbar toggled <onoff> for <target>",
                Placeholder.component("onoff", Component.text(result ? "on" : "off")
                        .color(result ? NamedTextColor.GREEN : NamedTextColor.RED)),
                Placeholder.parsed("target", player.getName()));

        return 1;
    }
}
