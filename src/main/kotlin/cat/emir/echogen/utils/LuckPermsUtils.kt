package cat.emir.echogen.utils

import org.bukkit.entity.Player

import net.luckperms.api.model.group.Group
import net.luckperms.api.model.user.User
import net.luckperms.api.query.Flag
import net.luckperms.api.query.QueryOptions
import cat.emir.echogen.Echogen

class LuckPermsUtils(val plugin: Echogen) {
    fun getUser(player: Player): User? {
        return plugin.luckPerms.userManager.getUser(player.uniqueId)
    }

    fun getPrefix(player: Player): String {
        return getUser(player)?.cachedData?.metaData?.prefix ?: ""
    }

    fun getSuffix(player: Player): String {
        return getUser(player)?.cachedData?.metaData?.suffix ?: ""
    }
    fun getPlayerGroups(player: Player): Set<Group> {
        val queryOptions = QueryOptions.defaultContextualOptions().toBuilder()
                .flag(Flag.RESOLVE_INHERITANCE, false)
                .build()
        return getUser(player)?.getInheritedGroups(queryOptions)?.toSet() ?: setOf()
    }

    fun getAllGroups(): Set<Group> {
        return plugin.luckPerms.groupManager.loadedGroups.toSet()
    }
}
