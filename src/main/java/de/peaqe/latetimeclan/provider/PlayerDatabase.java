package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.util.PlayerProperty;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 13:49 Uhr
 * *
 */

public class PlayerDatabase extends DatabaseProvider {

    private final ConcurrentMap<String, Optional<UUID>> playerCache;

    public PlayerDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
        this.playerCache = new ConcurrentHashMap<>();
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

            // Update cache after successful registration
            playerCache.put(player.getName().toLowerCase(), Optional.of(player.getUniqueId()));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public Optional<UUID> getUniqueId(@NotNull String playerName) {

        var cachedUniqueId = playerCache.get(playerName.toLowerCase());
        if (cachedUniqueId != null) {
            return cachedUniqueId;
        }

        var query = "SELECT * FROM latetime.player WHERE `" + PlayerProperty.NAME.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, playerName.toLowerCase());
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var uuid = UUID.fromString(resultSet.getString(PlayerProperty.UUID.getValue()));
                playerCache.put(playerName.toLowerCase(), Optional.of(uuid));
                return Optional.of(uuid);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return Optional.empty();
    }

    public String getName(UUID playerUniqueId) {

        var playerName = Bukkit.getOfflinePlayer(playerUniqueId).getName();
        if (playerName != null) {
            return playerName;
        }

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
