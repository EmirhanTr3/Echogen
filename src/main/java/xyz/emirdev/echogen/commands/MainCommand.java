package xyz.emirdev.echogen.commands;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.PluginCommand;

public class MainCommand extends PluginCommand {

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("echogen")
                .requires(ctx -> ctx.getSender().hasPermission("echogen.admin"))
                .then(Commands.literal("reload")
                        .executes(this::reload))
                .build();
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        long startedAt = System.currentTimeMillis();

        sender.sendRichMessage("<aqua>Reloading Echogen configuration...");

        Echogen.get().reloadConfig();

        sender.sendRichMessage(
                "<aqua>Reloaded Echogen configuration in <u><ms></u>ms.",
                Placeholder.unparsed("ms", String.valueOf(System.currentTimeMillis() - startedAt)));

        return 1;
    }
}
