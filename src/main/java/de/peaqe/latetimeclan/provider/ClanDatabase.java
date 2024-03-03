package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.util.ClanDecoder;
import de.peaqe.latetimeclan.provider.util.Property;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
    //private final SimpleCache simpleCache;

    public ClanDatabase(String hostname, String username, String password, String database, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;

        this.createTableIfNotExists();
        //this.simpleCache = new SimpleCache();
    }

    public void createTableIfNotExists() {
        this.connect();
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
        } finally {
            this.close();
        }
    }

    public void close() {
        try {
            Bukkit.getConsoleSender().sendMessage("§bSQL §7»» §cConnection Closed");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        try {
            Bukkit.getConsoleSender().sendMessage("§bSQL §7»» §aConnection Opened");
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

        if (clanModel == null || clanModel.getTag() == null) return;

        if (this.clanExists(clanModel.getTag())) {
            if (Objects.equals(this.getClan(clanModel.getTag()), clanModel)) return;
            this.updateClan(clanModel);
            return;
        }

        this.connect();
        try {

            var statement = this.connection.prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag().toLowerCase());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));

            //this.simpleCache.cache(clanModel);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public boolean clanExists(String clanTag) {
        //if (this.simpleCache.containsKey(clanTag.toLowerCase())) {
        //    return true;
        //}

        this.connect();
        try {
            var query = "SELECT * FROM latetime.clan WHERE " + Property.TAG.getValue() + " = ?";
            var statement = this.connection.prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                //var clanModel = new ClanModel(
                //        resultSet.getString(Property.NAME.getValue()),
                //        resultSet.getString(Property.TAG.getValue()),
                //        resultSet.getString(Property.CLAN_FOUNDER_UUID.getValue()),
                //        resultSet.getString(Property.CLAN_INVITATION_STATUS.getValue()),
                //        resultSet.getInt(Property.MAX_SIZE.getValue()),
                //        ClanDecoder.stringToMap(resultSet.getString(Property.MEMBERS.getValue()))
                //);

                //this.simpleCache.cache(clanModel);
                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    @Nullable
    public ClanModel getClan(String clanTag) {
        //if (this.simpleCache.containsKey(clanTag) && this.simpleCache.containsKey(clanTag)) {
        //    return this.simpleCache.get(clanTag);
        //}

        this.connect();
        try {
            final var query = "SELECT * FROM latetime.clan WHERE " + Property.TAG.getValue() + " = ?";
            var statement = this.connection.prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

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

                //this.simpleCache.cache(clanModel);
                return clanModel;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return null;
    }

    public void updateClan(ClanModel clanModel) {

        final var query = "UPDATE latetime.clan SET " +
                Property.NAME.getValue() + " = ?, " +
                Property.TAG.getValue() + " = ?, " +
                Property.CLAN_FOUNDER_UUID.getValue() + " = ?, " +
                Property.CLAN_INVITATION_STATUS.getValue() + " = ?, " +
                Property.MAX_SIZE.getValue() + " = ?, " +
                Property.MEMBERS.getValue() + " = ? " +
                "WHERE " + Property.TAG.getValue() + " = ?";

        if (this.getClan(clanModel.getTag()) == null) {
            this.createClan(clanModel);
            return;
        }

        this.connect();
        try {

            var statement = this.connection.prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag().toLowerCase());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));
            statement.setString(7, clanModel.getTag());

            //this.simpleCache.remove(clanModel.getTag());
            //this.simpleCache.cache(clanModel);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    @Nullable
    public ClanModel getClanModelByCondition(Property property, Object conditionItem) throws SQLException {

        var sql = "SELECT * FROM latetime.clan WHERE " + property.getValue() + " = ?";

        this.connect();

        var preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.setObject(1, conditionItem);

        var resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {

            var clanModel = new ClanModel(
                    resultSet.getString(Property.NAME.getValue()),
                    resultSet.getString(Property.TAG.getValue()),
                    resultSet.getString(Property.CLAN_FOUNDER_UUID.getValue()),
                    resultSet.getString(Property.CLAN_INVITATION_STATUS.getValue()),
                    resultSet.getInt(Property.MAX_SIZE.getValue()),
                    ClanDecoder.stringToMap(resultSet.getString(Property.MEMBERS.getValue()))
            );

            //this.simpleCache.cache(clanModel);

            return clanModel;
        }

        return null;
    }

    public Map<UUID, ClanGroupModel> getAllPlayersInClan(String clanTag) {

        var players = new HashMap<UUID, ClanGroupModel>();
        this.connect();

        try {
            var query = "SELECT * FROM latetime.clan WHERE " + Property.TAG.getValue() + " = ?";
            var statement = this.connection.prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return ClanDecoder.stringToMap(resultSet.getString(Property.MEMBERS.getValue()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return players;
    }

    public ClanModel getClanModelOfMember(UUID memberUUID) {

        var clanModel = (ClanModel) null;
        this.connect();

        try {

            var query = "SELECT * FROM latetime.clan WHERE " + Property.MEMBERS.getValue() + " LIKE ?";
            var statement = this.connection.prepareStatement(query);

            statement.setString(1, "%" + memberUUID.toString() + "%");

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                clanModel = new ClanModel(
                        resultSet.getString(Property.NAME.getValue()),
                        resultSet.getString(Property.TAG.getValue()),
                        resultSet.getString(Property.CLAN_FOUNDER_UUID.getValue()),
                        resultSet.getString(Property.CLAN_INVITATION_STATUS.getValue()),
                        resultSet.getInt(Property.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(Property.MEMBERS.getValue()))
                );

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return clanModel;
    }

}
