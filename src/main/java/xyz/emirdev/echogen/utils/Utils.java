package xyz.emirdev.echogen.utils;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Utils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * Formats a MiniMessage into a component. To provide prefix, use
     * {@code \<prefix\>} tag.
     * 
     * @param message   The message you want to format.
     * @param resolvers Additional resolvers you need.
     * @return A resolved MiniMessage component.
     */
    public static Component formatMessage(String message, TagResolver... resolvers) {
        return miniMessage.deserialize(message, resolvers);
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
            components.add(miniMessage.deserialize(str, resolvers));
        }

        return components;
    }
}
