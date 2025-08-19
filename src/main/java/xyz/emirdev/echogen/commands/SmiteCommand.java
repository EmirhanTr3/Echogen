package xyz.emirdev.echogen.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echogen.PluginCommand;

public class SmiteCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("smite")
                .requires(hasPermission("echogen.smite"))
                .then(Commands.argument("players", ArgumentTypes.players())
                        .executes(this::smite))
                .build();
    }

    private int smite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getSender();
        List<Player> targets = getPlayers(ctx, "players");
        if (targets == null)
            return 1;

        for (Player target : targets) {
            target.getWorld().strikeLightningEffect(target.getLocation());

            sender.sendRichMessage(
                    "<aqua>You have smitten <player>!</aqua>",
                    Placeholder.unparsed("player", target.getName()));
        }

        return 1;
    }
}
