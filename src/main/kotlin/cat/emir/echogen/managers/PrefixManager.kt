package cat.emir.echogen.managers

import cat.emir.echogen.helpers.ConfigurateListHelper
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

import cat.emir.echogen.Echogen
import org.bukkit.entity.Player

class PrefixManager(val plugin: Echogen) {
    val prefixes = mutableListOf<Prefix>()

    fun getPrefix(id: String): Prefix? {
        return prefixes.firstOrNull { it.id == id }
    }

    fun getPlayerPrefix(player: Player): Prefix? {
        val currentPrefix = plugin.prefixDatabase.getPrefix(player)
        val prefix = if (currentPrefix != null) getPrefix(currentPrefix) else null

        if (prefix == null && currentPrefix != null) {
            plugin.prefixDatabase.deletePrefix(player)
        }

        return prefix
    }

    fun getPlayerPrefixString(player: Player): String? {
        return getPlayerPrefix(player)?.prefix
    }

    fun load() {
        prefixes.clear()

        if (!plugin.config.rootNode.node("chat", "prefix", "enabled").boolean)
            return

        val blacklistedGroups = plugin.config.rootNode.node("chat", "prefix", "blacklist")
            .getList(String::class.java) ?: listOf()

        val sortedGroups = plugin.luckPermsUtils.getAllGroups()
            .sortedBy { it.weight.orElse(0) }

        for (group in sortedGroups) {
            if (blacklistedGroups.contains(group.name)) {
                plugin.logger.info("Skipped LuckPerms group ${group.name} due to it being blacklisted from config.")
                continue
            }

            prefixes.add(Prefix(
                    "group-" + group.name,
                    "<aqua>" + group.friendlyName,
                    group.cachedData.metaData.prefix ?: "",
                    listOf("This prefix requires ${group.friendlyName} group.")))
            plugin.logger.info("Loaded prefix from LuckPerms group ${group.name}")
        }

        val prefixList = ConfigurateListHelper.getPrefixList(plugin.config.rootNode.node("chat", "prefix", "extra"))

        for (map in prefixList) {
            val id = map.entries.first().key
            val value = map.entries.first().value

            val name = value.name
            val prefix = value.prefix
            val description = value.description

            prefixes.add(Prefix(id, name, prefix, description))
            plugin.logger.info("Loaded extra prefix with id: $id name: $name prefix: $prefix description: $description")
        }
    }

    data class Prefix(val id: String, val name: String, val prefix: String, val description: List<String>)

    @ConfigSerializable
    data class PrefixValue(
        @Setting val name: String = "",
        @Setting val prefix: String = "",
        @Setting val description: List<String> = emptyList()
    )
}
