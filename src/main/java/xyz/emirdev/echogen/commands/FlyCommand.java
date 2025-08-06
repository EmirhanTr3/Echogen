package xyz.emirdev.echogen.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echogen.PluginCommand;

public class FlyCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("fly")
                .requires(ctx -> ctx.getSender().hasPermission("echogen.fly"))
                .executes(this::fly)
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(ctx -> ctx.getSender().hasPermission("echogen.fly.others"))
                        .executes(this::flyOther))
                .build();
    }

    private int fly(CommandContext<CommandSourceStack> ctx) {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        boolean allowFlight = player.getAllowFlight();

        player.setAllowFlight(!allowFlight);

        if (!allowFlight)
            player.sendRichMessage("<aqua>You have <green>enabled</green> flight mode.</aqua>");
        else
            player.sendRichMessage("<aqua>You have <red>disabled</red> flight mode.</aqua>");

        return 1;
    }

    private int flyOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        Player target = getPlayer(ctx, "player");

        boolean allowFlight = target.getAllowFlight();

        target.setAllowFlight(!allowFlight);

        TagResolver playerTag = Placeholder.unparsed("player", target.getName());

        if (!allowFlight) {
            player.sendRichMessage("<aqua>You have <green>enabled</green> flight mode for <player>.</aqua>", playerTag);
            target.sendRichMessage("<aqua>Your flight mode was <green>enabled</green>.</aqua>");
        } else {
            player.sendRichMessage("<aqua>You have <red>disabled</red> flight mode for <player>.</aqua>", playerTag);
            target.sendRichMessage("<aqua>Your flight mode was <red>disabled</red>.</aqua>");
        }

        return 1;
    }
}
