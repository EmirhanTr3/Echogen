package xyz.emirdev.echogen.events;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.commands.ChatCommand;
import xyz.emirdev.echogen.managers.FilterManager.FilterElement;
import xyz.emirdev.echogen.utils.LuckPermsUtils;
import xyz.emirdev.echogen.utils.TimeUtils;
import xyz.emirdev.echogen.utils.Utils;

public class ChatEvent implements Listener {
    public static final Map<UUID, Long> slowmodePlayers = new HashMap<>();

    public static Component getChatFormat(Player player, Component message, String prefix) {
        CommentedConfigurationNode rootNode = Echogen.get().getPluginConfig().getRootNode();

        Component component = player.hasPermission("echogen.chat.component")
                ? MiniMessage.miniMessage()
                        .deserialize(PlainTextComponentSerializer.plainText().serialize(message))
                : message;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!item.getType().isAir() && rootNode.node("chat", "item", "enabled").getBoolean()) {
            boolean requiresPermission = rootNode.node("chat", "item", "permission").getBoolean();
            if (!requiresPermission || player.hasPermission("echogen.chat.item")) {
                component = component.replaceText(c -> c
                        .matchLiteral("[item]")
                        .replacement(item.displayName().hoverEvent(item.asHoverEvent()))
                        .build());
            }
        }

        String format = rootNode.node("chat", "format").getString();

        return Utils.formatMessage(format,
                Placeholder.parsed("prefix", prefix != null ? prefix : LuckPermsUtils.getPrefix(player)),
                Placeholder.parsed("suffix", LuckPermsUtils.getSuffix(player)),
                Placeholder.parsed("name", player.getName()),
                Placeholder.component("message", component));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void chatEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CommentedConfigurationNode rootNode = Echogen.get().getPluginConfig().getRootNode();
        if (!rootNode.node("chat", "enabled").getBoolean())
            return;

        String prefix = Echogen.get().getPrefixManager().getPlayerPrefixString(player);
        Component component = getChatFormat(player, event.message(), prefix);

        event.renderer((p, d, m, a) -> component);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void chatCmdEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (ChatCommand.chatMuted && !player.hasPermission("echogen.chat.mute.bypass")) {
            player.sendRichMessage("<red>Chat is currently muted.</red>");
            event.setCancelled(true);
            return;
        }

        if (ChatCommand.slowmode != null && !player.hasPermission("echogen.chat.slowmode.bypass")) {
            if (!slowmodePlayers.containsKey(uuid)
                    || System.currentTimeMillis() > (slowmodePlayers.get(uuid) + ChatCommand.slowmode.toMillis())) {
                slowmodePlayers.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                long expiresAt = slowmodePlayers.get(uuid) + ChatCommand.slowmode.toMillis();
                Duration timeLeft = Duration.ofMillis(expiresAt - System.currentTimeMillis());

                player.sendRichMessage(
                        "<red>You cannot send a message for <dark_red><duration></dark_red>.</red>",
                        Placeholder.unparsed("duration", TimeUtils.parseDurationToString(timeLeft)));
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void chatFilterEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("echogen.chat.filter.bypass"))
            return;

        List<FilterElement> filters = Echogen.get().getFilterManager().getFilters();
        if (filters.isEmpty())
            return;

        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        for (FilterElement filter : filters) {
            Matcher matcher = filter.getPattern().matcher(message);

            while (matcher.find()) {
                switch (filter.getType()) {
                    case CENSOR:
                        message = message.replace(matcher.group(0), matcher.group(0).replaceAll(".", "*"));
                        break;
                    case BLOCK_MESSAGE:
                        event.setCancelled(true);
                        player.sendRichMessage("<red>Your message has been filtered.</red>");
                        return;
                    case COMMAND:
                        event.setCancelled(true);
                        player.sendRichMessage("<red>Your message has been filtered.</red>");
                        Bukkit.getScheduler().runTask(Echogen.get(), () -> {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                    filter.getCommand()
                                            .replaceAll("<player>", player.getName())
                                            .replaceAll("<uuid>", player.getUniqueId().toString()));
                        });
                        return;
                }
            }
        }

        event.message(PlainTextComponentSerializer.plainText().deserialize(message));
    }
}
