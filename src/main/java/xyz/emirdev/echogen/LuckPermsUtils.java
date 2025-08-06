package xyz.emirdev.echogen;

import java.util.Objects;

import org.bukkit.entity.Player;

import net.luckperms.api.model.user.User;

public class LuckPermsUtils {
    public static User getUser(Player player) {
        return Echogen.get().getLuckPerms().getUserManager().getUser(player.getUniqueId());
    }

    public static String getPrefix(Player player) {
        return Objects.requireNonNullElse(getUser(player).getCachedData().getMetaData().getPrefix(), "");
    }

    public static String getSuffix(Player player) {
        return Objects.requireNonNullElse(getUser(player).getCachedData().getMetaData().getSuffix(), "");
    }
}
