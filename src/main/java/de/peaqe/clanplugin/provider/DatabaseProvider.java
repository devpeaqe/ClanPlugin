package de.peaqe.clanplugin.provider;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.util.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 13:51 Uhr
 * *
 */

public abstract class DatabaseProvider {

    private Connection connection;
    private final DatabaseConnection databaseConnection;

    public DatabaseProvider(ClanPlugin clanPlugin) {
        this.databaseConnection = clanPlugin.getDatabaseConnection();
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + this.databaseConnection.hostname() + ":" +
                            this.databaseConnection.port() + "/" + this.databaseConnection.database(),
                    this.databaseConnection.username(),
                    this.databaseConnection.password()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
