package xyz.emirdev.echogen.database;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.h2.jdbc.JdbcConnection;

import xyz.emirdev.echogen.Echogen;

public class Database {
    private static final Path file = Path.of(
            "~",
            Echogen.get().getDataPath().toString(),
            "database");

    public static Connection createConnection() throws SQLException {
        return new JdbcConnection(
                "jdbc:h2:" + file,
                new Properties(),
                null,
                null,
                false);
    }
}
