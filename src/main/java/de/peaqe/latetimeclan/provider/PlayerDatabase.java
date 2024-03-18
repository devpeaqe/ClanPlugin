package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.util.PlayerProperty;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 13:49 Uhr
 * *
 */

public class PlayerDatabase extends DatabaseProvider {

    public PlayerDatabase() {
        super(LateTimeClan.getInstance());
        this.createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.player (" +
                    "  `" + PlayerProperty.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + PlayerProperty.UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  PRIMARY KEY (`" + PlayerProperty.UUID.getValue() + "`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void registerPlayer(@NotNull Player player) {

        final var query = "INSERT INTO latetime.player (`" + PlayerProperty.NAME.getValue() + "`, `" +
                PlayerProperty.UUID.getValue() + "`) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE `" + PlayerProperty.NAME.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, player.getName().toLowerCase());
            statement.setString(2, player.getUniqueId().toString());
            statement.setString(3, player.getName().toLowerCase());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public UUID getUniqueId(@NotNull String playerName) {

        var query = "SELECT * FROM latetime.player WHERE `" + PlayerProperty.NAME.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, playerName.toLowerCase());
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return UUID.fromString(resultSet.getString(PlayerProperty.UUID.getValue()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return null;
    }

    public String getName(UUID playerUniqueId) {

        var query = "SELECT * FROM latetime.player WHERE `" + PlayerProperty.UUID.getValue() + "` = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, playerUniqueId.toString());
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString(PlayerProperty.NAME.getValue());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return "Unknown";
    }

}
