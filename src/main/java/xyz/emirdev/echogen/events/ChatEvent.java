package xyz.emirdev.echogen.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import xyz.emirdev.echogen.Echogen;
import xyz.emirdev.echogen.LuckPermsUtils;
import xyz.emirdev.echogen.Utils;

public class ChatEvent implements Listener {

    @EventHandler
    public void chatEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        CommentedConfigurationNode rootNode = Echogen.get().getPluginConfig().getRootNode();
        if (rootNode.node("chat", "enabled").getBoolean() == false)
            return;

        Component message = player.hasPermission("echogen.chat.component")
                ? MiniMessage.miniMessage()
                        .deserialize(PlainTextComponentSerializer.plainText().serialize(event.message()))
                : event.message();

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && !item.getType().isAir() && rootNode.node("chat", "item", "enabled").getBoolean() == true) {
            boolean requiresPermission = rootNode.node("chat", "item", "permission").getBoolean();
            if (!requiresPermission || player.hasPermission("echogen.chat.item")) {
                message = message.replaceText(c -> c
                        .matchLiteral("[item]")
                        .replacement(item.displayName().hoverEvent(item.asHoverEvent()))
                        .build());
            }
        }

        String format = rootNode.node("chat", "format").getString();

        Component component = Utils.formatMessage(format,
                Placeholder.parsed("prefix", LuckPermsUtils.getPrefix(player)),
                Placeholder.parsed("suffix", LuckPermsUtils.getSuffix(player)),
                Placeholder.parsed("name", player.getName()),
                Placeholder.component("message", message));

        event.renderer((p, d, m, a) -> component);
    }
}
