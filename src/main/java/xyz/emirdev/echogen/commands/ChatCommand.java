package xyz.emirdev.echogen.commands;

import java.time.Duration;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import xyz.emirdev.echogen.PluginCommand;
import xyz.emirdev.echogen.utils.TimeUtils;

public class ChatCommand extends PluginCommand {
    public static boolean chatMuted = false;
    public static Duration slowmode = null;

    @Override
    public LiteralCommandNode<CommandSourceStack> getCommand() {
        return Commands.literal("chat")
                .requires(hasPermission("echogen.chat"))
                .then(Commands.literal("clear")
                        .requires(hasPermission("echogen.chat.clear"))
                        .executes(this::clear))
                .then(Commands.literal("mute")
                        .requires(hasPermission("echogen.chat.mute"))
                        .executes(this::mute))
                .then(Commands.literal("slowmode")
                        .requires(hasPermission("echogen.chat.slowmode"))
                        .then(Commands.literal("clear")
                                .executes(this::slowmodeClear))
                        .then(Commands.argument("duration", StringArgumentType.word())
                                .executes(this::slowmode)))
                .build();
    }

    private int clear(CommandContext<CommandSourceStack> ctx) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("echogen.chat.clear.bypass"))
                continue;

            for (int i = 1; i <= 1000; i++) {
                player.sendMessage(" ".repeat(i % 60));
            }
        }

        Bukkit.getServer().sendRichMessage("<aqua>Chat has been cleared.</aqua>");

        return 1;
    }

    private int mute(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        TagResolver playerTag = Placeholder.unparsed("player", sender.getName());

        if (!chatMuted) {
            chatMuted = true;
            Bukkit.getServer().sendRichMessage("<aqua>Chat has been <red>muted</red> by <player>.</aqua>", playerTag);
        } else {
            chatMuted = false;
            Bukkit.getServer().sendRichMessage("<aqua>Chat has been <green>unmuted</green> by <player>.</aqua>",
                    playerTag);
        }

        return 1;
    }

    private int slowmode(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Duration duration = getDuration(ctx, "duration");
        if (duration == null)
            return 1;

        slowmode = duration;

        Bukkit.getServer().sendRichMessage(
                "<aqua>Chat slowmode has been set to <dark_aqua><duration></dark_aqua> by <player>.</aqua>",
                Placeholder.unparsed("duration", TimeUtils.parseDurationToString(duration)),
                Placeholder.unparsed("player", sender.getName()));

        return 1;
    }

    private int slowmodeClear(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();

        slowmode = null;

        Bukkit.getServer().sendRichMessage(
                "<aqua>Chat slowmode has been <dark_aqua>disabled</dark_aqua> by <player>.</aqua>",
                Placeholder.unparsed("player", sender.getName()));

        return 1;
    }
}
