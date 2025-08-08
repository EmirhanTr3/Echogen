package xyz.emirdev.echogen.database;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrefixDatabase {
    private static final Map<UUID, String> cache = new HashMap<>();

    private static final String CREATE = "CREATE TABLE IF NOT EXISTS prefix_table (uuid text, prefix text)";
    private static final String SELECT = "SELECT prefix FROM prefix_table WHERE uuid=? LIMIT 1";
    private static final String SET = "MERGE INTO prefix_table (uuid, prefix) KEY (uuid) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM prefix_table WHERE uuid=?";

    public PrefixDatabase() {
        try (Connection c = getConnection()) {
            c.prepareStatement(CREATE).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return Database.createConnection();
    }

    public String getPrefix(OfflinePlayer player) {
        if (cache.containsKey(player.getUniqueId())) {
            return cache.get(player.getUniqueId());
        }

        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(SELECT)) {
                ps.setString(1, player.getUniqueId().toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String prefix = rs.getString("prefix");
                        cache.put(player.getUniqueId(), prefix);
                        return prefix;
                    } else {
                        cache.put(player.getUniqueId(), null);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setPrefix(Player player, String prefixId) {
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(SET)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, prefixId);
                ps.execute();
            }
            cache.put(player.getUniqueId(), prefixId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePrefix(Player player) {
        try (Connection c = getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(DELETE)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.execute();
            }
            cache.remove(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
