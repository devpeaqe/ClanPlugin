package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.util.ClanDecoder;
import de.peaqe.latetimeclan.provider.cache.DatabaseCache;
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
    private final DatabaseCache databaseCache;

    public ClanDatabase(String hostname, String username, String password, String database, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;

        this.connect();
        this.createTableIfNotExists();
        this.close();

        this.databaseCache = new DatabaseCache();

    }

    public void createTableIfNotExists() {
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.clan (" +
                    "  `" + Property.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.CLAN_FOUNDER_UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.CLAN_INVITATION_STATUS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + Property.MAX_SIZE.getValue() + "` INT NOT NULL," +
                    "  `" + Property.MEMBERS.getValue() + "` VARCHAR(255) NOT NULL," +
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

    public void createClan(ClanModel clanModel) {

        final var query = "INSERT INTO latetime.clan (" +
                Property.NAME.getValue() + ", " +
                Property.TAG.getValue() + ", " +
                Property.CLAN_FOUNDER_UUID.getValue() + ", " +
                Property.CLAN_INVITATION_STATUS.getValue() + ", " +
                Property.MAX_SIZE.getValue() + ", " +
                Property.MEMBERS.getValue() +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        if (this.databaseCache.containsValue(clanModel)) {
            return;
        }

        this.connect();
        try {

            var statement = this.connection.prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));

            this.databaseCache.addEntry(clanModel.getTag(), clanModel);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateClan(ClanModel clanModel) {


        final var query = "UPDATE latetime.clan SET " +
                "name = ?, tag = ?, clan_founder_uuid = ?, clan_invitation_status = ?, max_size = ?, members = ? " +
                "WHERE tag = ?";

        if (!this.databaseCache.containsValue(clanModel)) {
            return;
        }

        this.connect();
        try {

            var statement = this.connection.prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));
            statement.setString(7, clanModel.getTag());

            this.databaseCache.removeEntry(clanModel.getTag());
            this.databaseCache.addEntry(clanModel.getTag(), clanModel);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ClanModel getClan(String clanTag) {

        final var query = "SELECT * FROM latetime.clan WHERE tag = ?";

        if (this.databaseCache.containsKey(clanTag)) {
            return this.databaseCache.getEntry(clanTag);
        }

        this.connect();
        try {
            var statement = this.connection.prepareStatement(query);
            statement.setString(1, clanTag);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {

                var clanModel = new ClanModel(
                        resultSet.getString(Property.NAME.getValue()),
                        resultSet.getString(Property.TAG.getValue()),
                        resultSet.getString(Property.CLAN_FOUNDER_UUID.getValue()),
                        resultSet.getString(Property.CLAN_INVITATION_STATUS.getValue()),
                        resultSet.getInt(Property.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(Property.MEMBERS.getValue()))
                );

                this.databaseCache.addEntry(clanTag, clanModel);
                return clanModel;

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
