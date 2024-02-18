package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.provider.util.Property;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:16 Uhr
 * *
 */

public class ClanDatabase {

    private final String hostname, username, password, database;
    private final int port;
    private Connection connection;

    public ClanDatabase(String hostname, String username, String password, String database, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;

        this.connect();
        this.createTableIfNotExists();
        this.close();

    }

    public void createTableIfNotExists() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.clan (" +
                    "  `" + Property.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.CLAN_FOUNDER_UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.CLAN_INVITATION_STATUS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.MAX_SIZE.getValue() + "` INT NOT NULL," +
                    "  PRIMARY KEY (`tag`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + ":" + port + "/" + database,
                    username,
                    password
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




}
