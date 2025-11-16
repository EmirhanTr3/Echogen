package xyz.emirdev.echogen.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.emirdev.echogen.PluginCommand;

import java.util.List;
import java.util.Set;

public class SudoCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("sudo")
                .requires(hasPermission("echogen.sud"))
                .then(Commands.argument("players", ArgumentTypes.players())
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(this::sudo)))
                .build();
    }

    private int sudo(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        List<Player> targets = getPlayers(ctx, "players");
        if (targets == null)
            return 1;

        String message = StringArgumentType.getString(ctx, "message");

        for (Player target : targets) {
            target.chat(message);

            sender.sendRichMessage(
                    "<aqua>You have made <dark_aqua><player></dark_aqua> send <dark_aqua><message></dark_aqua></aqua>",
                    Placeholder.unparsed("player", target.getName()),
                    Placeholder.unparsed("message", message));
        }

        return 1;
    }
}
