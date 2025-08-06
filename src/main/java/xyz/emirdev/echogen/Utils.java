package xyz.emirdev.echogen;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Utils {
    private static MiniMessage minimessage = MiniMessage.miniMessage();

    /**
     * Formats a MiniMessage into a component. To provide prefix, use
     * {@code \<prefix\>} tag.
     * 
     * @param message   The message you want to format.
     * @param resolvers Additional resolvers you need.
     * @return A resolved MiniMessage component.
     */
    public static Component formatMessage(String message, TagResolver... resolvers) {
        return minimessage.deserialize(message, resolvers);
    }

    /**
     * Converts a component back into a MiniMessage string.
     * 
     * @param component The component you want to convert.
     * @return The MiniMessage string of the component.
     */
    public static String unformatMessage(Component component) {
        return minimessage.serialize(component);
    }

    /**
     * Sends a message to a CommandSender.
     * 
     * @param sender    The sender you want to send the message to.
     * @param message   The message you want to send, formatted in minimessage.
     * @param resolvers Additional resolvers you need.
     */
    public static void sendMessage(CommandSender sender, String message, TagResolver... resolvers) {
        sender.sendMessage(formatMessage(message, resolvers));
    }

    /**
     * Broadcasts a message to everyone.
     * 
     * @param message   The message you want to send, formatted in minimessage.
     * @param resolvers Additional resolvers you need.
     */
    public static void broadcast(String message, TagResolver... resolvers) {
        Bukkit.getServer().sendMessage(formatMessage(message, resolvers));
    }

    /**
     * Broadcasts a message to everyone with the specified permission.
     * 
     * @param perm      The permission needed to see the message.
     * @param message   The message you want to send, formatted in minimessage.
     * @param resolvers Additional resolvers you need.
     */
    public static void broadcastPermission(String perm, String message, TagResolver... resolvers) {
        Bukkit.getOnlinePlayers()
                .stream().filter(p -> p.hasPermission("perm"))
                .forEach(p -> p.sendMessage(formatMessage(message, resolvers)));
        Bukkit.getConsoleSender().sendMessage(formatMessage(message, resolvers));
    }

    /**
     * A convenience method to format a message with multiline (\n).
     * 
     * @param string    The message you want to format.
     * @param resolvers Additional resolvers you need.
     * @return Multiple components that are multiline.
     */
    public static List<Component> formatMultiline(String string, TagResolver... resolvers) {
        List<Component> components = new ArrayList<>();
        String[] strings = string.split("\n");

        for (String str : strings) {
            components.add(minimessage.deserialize(str, resolvers));
        }

        return components;
    }
}
