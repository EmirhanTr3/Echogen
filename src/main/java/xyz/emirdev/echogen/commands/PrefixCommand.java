package xyz.emirdev.echogen.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.PluginCommand;
import xyz.emirdev.echogen.guis.PrefixGUI;

public class PrefixCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("prefix")
                .requires(ctx -> ctx.getSender().hasPermission("echogen.chat.prefix")
                        && Echogen.get().getPluginConfig().getRootNode().node("chat", "prefix", "enabled").getBoolean())
                .executes(this::execute)
                .build();
    }

    private int execute(CommandContext<CommandSourceStack> ctx) {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        PrefixGUI.openGUI(player);

        return 1;
    }
}
