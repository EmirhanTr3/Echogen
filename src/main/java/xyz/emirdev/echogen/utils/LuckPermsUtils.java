package xyz.emirdev.echogen.utils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.bukkit.entity.Player;

import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import xyz.emirdev.echogen.Echogen;

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

    public static Collection<Group> getPlayerGroups(Player player) {
        return getUser(player).getInheritedGroups(QueryOptions.defaultContextualOptions());
    }

    public static Set<Group> getAllGroups() {
        return Echogen.get().getLuckPerms().getGroupManager().getLoadedGroups();
    }
}
