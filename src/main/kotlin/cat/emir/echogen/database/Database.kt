package cat.emir.echogen.database

import java.nio.file.Path
import java.sql.Connection
import java.util.Properties

import org.h2.jdbc.JdbcConnection

import cat.emir.echogen.Echogen

class Database(val plugin: Echogen) {
    val file: Path = Path.of(
        "~",
        plugin.dataPath.toString(),
        "database"
    )

    fun createConnection(): Connection {
        return JdbcConnection(
            "jdbc:h2:$file",
            Properties(),
            null,
            null,
            false
        )
    }
}
