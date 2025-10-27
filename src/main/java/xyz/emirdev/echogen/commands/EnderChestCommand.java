package xyz.emirdev.echogen.commands;

import org.bukkit.entity.Player;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import xyz.emirdev.echogen.PluginCommand;

import java.util.Set;

public class EnderChestCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("enderchest")
                .requires(hasPermission("echogen.enderchest"))
                .executes(this::enderchest)
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(hasPermission("echogen.enderchest.others"))
                        .executes(this::enderchestOther))
                .build();
    }

    @Override
    public Set<String> getAliases() {
        return Set.of("ec");
    }

    private int enderchest(CommandContext<CommandSourceStack> ctx) {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        player.openInventory(player.getEnderChest());

        return 1;
    }

    private int enderchestOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = getContextPlayer(ctx);
        if (player == null)
            return 1;

        Player target = getPlayer(ctx, "player");

        player.openInventory(target.getEnderChest());

        return 1;
    }
}
