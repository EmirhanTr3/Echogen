package cat.emir.echogen.database

import cat.emir.echogen.Echogen
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.sql.Connection
import java.util.UUID

class PrefixDatabase(val plugin: Echogen) {
    val cache = mutableMapOf<UUID, String?>()

    val CREATE = "CREATE TABLE IF NOT EXISTS prefix_table (uuid text, prefix text)"
    val SELECT = "SELECT prefix FROM prefix_table WHERE uuid=? LIMIT 1"
    val SET = "MERGE INTO prefix_table (uuid, prefix) KEY (uuid) VALUES (?, ?)"
    val DELETE = "DELETE FROM prefix_table WHERE uuid=?"

    fun load() {
        getConnection().use { c ->
            c.prepareStatement(CREATE).execute()
        }
    }

    fun getConnection(): Connection {
        return Database(plugin).createConnection()
    }

    fun getPrefix(player: OfflinePlayer): String? {
        if (cache.containsKey(player.uniqueId))
            return cache[player.uniqueId]

        getConnection().use { c ->
            val ps = c.prepareStatement(SELECT)
            ps.setString(1, player.uniqueId.toString())
            ps.executeQuery().use { rs ->
                if (rs.next()) {
                    val prefix = rs.getString("prefix")
                    cache[player.uniqueId] = prefix
                    return prefix
                } else {
                    cache[player.uniqueId] = null
                    return null
                }
            }
        }
    }

    fun setPrefix(player: Player, prefixId: String) {
        getConnection().use { c ->
            val ps = c.prepareStatement(SET)
            ps.setString(1, player.uniqueId.toString())
            ps.setString(2, prefixId)
            ps.execute()
            cache.put(player.uniqueId, prefixId)
        }
    }

    fun deletePrefix(player: Player) {
        getConnection().use { c ->
            val ps = c.prepareStatement(DELETE)
            ps.setString(1, player.uniqueId.toString())
            ps.execute()
            cache.remove(player.uniqueId)
        }
    }
}
