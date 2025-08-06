package xyz.emirdev.echogen;

import java.util.List;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;

public abstract class PluginCommand {
    public abstract LiteralCommandNode<CommandSourceStack> getCommand();

    public Player getContextPlayer(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getSender() instanceof Player player)
            return player;
        ctx.getSource().getSender().sendRichMessage("<red>You cannot run this command as console.</red>");
        return null;
    }

    public Player getPlayer(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        Player player = ctx.getArgument(name, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
        if (player == null) {
            ctx.getSource().getSender().sendRichMessage("<red>No player was found</red>");
            return null;
        }
        return player;
    }

    public List<Player> getPlayers(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        List<Player> players = ctx.getArgument(name, PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        if (players == null || players.isEmpty()) {
            ctx.getSource().getSender().sendRichMessage("<red>No player was found</red>");
            return null;
        }
        return players;
    }
}
