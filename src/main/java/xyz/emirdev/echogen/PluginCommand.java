package xyz.emirdev.echogen;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import xyz.emirdev.echogen.utils.TimeUtils;

public abstract class PluginCommand {
    public abstract LiteralCommandNode<CommandSourceStack> getCommand();

    public Predicate<CommandSourceStack> hasPermission(String permission) {
        return ctx -> ctx.getSender().hasPermission(permission);
    }

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

    public Duration getDuration(CommandContext<CommandSourceStack> ctx, String name) {
        String durationString = StringArgumentType.getString(ctx, name);
        Duration duration = TimeUtils.convertStringToDuration(durationString);

        if (duration == null) {
            ctx.getSource().getSender().sendRichMessage("<red>Invalid duration provided</red>");
            return null;
        }

        return duration;
    }
}
