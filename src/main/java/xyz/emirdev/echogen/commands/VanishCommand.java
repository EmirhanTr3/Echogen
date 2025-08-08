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
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.PluginCommand;

public class VanishCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("vanish")
                .requires(hasPermission("echogen.vanish"))
                .executes(this::vanish)
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(hasPermission("echogen.vanish.others"))
                        .executes(this::vanishOther))
                .build();
    }

    private int vanish(CommandContext<CommandSourceStack> ctx) {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        boolean vanished = Echogen.get().getVanishManager().isVanished(player);

        if (!vanished) {
            Echogen.get().getVanishManager().vanish(player);
            player.sendRichMessage("<aqua>You have <green>enabled</green> vanish.</aqua>");
        } else {
            Echogen.get().getVanishManager().unVanish(player);
            player.sendRichMessage("<aqua>You have <red>disabled</red> vanish.</aqua>");
        }

        return 1;
    }

    private int vanishOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        Player target = getPlayer(ctx, "player");

        boolean vanished = Echogen.get().getVanishManager().isVanished(target);

        TagResolver playerTag = Placeholder.unparsed("player", target.getName());

        if (!vanished) {
            Echogen.get().getVanishManager().vanish(target);
            player.sendRichMessage("<aqua>You have <green>enabled</green> vanish for <player>.</aqua>", playerTag);
            target.sendRichMessage("<aqua>Your vanish has been <green>enabled</green>.</aqua>");
        } else {
            Echogen.get().getVanishManager().unVanish(target);
            player.sendRichMessage("<aqua>You have <red>disabled</red> vanish for <player>.</aqua>", playerTag);
            target.sendRichMessage("<aqua>Your vanish has been <red>disabled</red>.</aqua>");
        }

        return 1;
    }
}
