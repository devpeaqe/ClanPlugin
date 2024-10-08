package de.peaqe.clanplugin.provider.database;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanObject;
import de.peaqe.clanplugin.objects.SettingsObject;
import de.peaqe.clanplugin.provider.DatabaseProvider;
import de.peaqe.clanplugin.provider.util.ClanSettingsProperty;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:11 Uhr
 * *
 */

public class ClanSettingsDatabase extends DatabaseProvider {

    private final ConcurrentMap<String, Optional<SettingsObject>> settingsCache;

    public ClanSettingsDatabase(ClanPlugin clanPlugin) {
        super(clanPlugin);
        this.settingsCache = new ConcurrentHashMap<>();
        this.createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS novia.clan_settings (" +
                    "  `" + ClanSettingsProperty.CLAN_TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue() + "` BOOLEAN NOT NULL," +
                    "  `" + ClanSettingsProperty.CLAN_BANK_TOGGLED.getValue() + "` BOOLEAN NOT NULL," +
                    "  PRIMARY KEY (`" + ClanSettingsProperty.CLAN_TAG.getValue() + "`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    void insertClan(@NotNull ClanObject clanObject) {
        var settings = clanObject.getSettings();
        if (settings == null) return;

        var query = "INSERT INTO novia.clan_settings VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `" + ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue() + "` = ?," +
                "`" + ClanSettingsProperty.CLAN_BANK_TOGGLED.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, clanObject.getTag().toUpperCase());
            statement.setBoolean(2, clanObject.getSettings().isClanChatToggled());
            statement.setBoolean(3, clanObject.getSettings().isClanBankToggled());

            statement.setBoolean(4, clanObject.getSettings().isClanChatToggled());
            statement.setBoolean(5, clanObject.getSettings().isClanBankToggled());

            statement.executeUpdate();

            // Update cache after successful insertion
            settingsCache.put(clanObject.getTag().toUpperCase(), Optional.of(settings));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void deleteClan(ClanObject clan) {
        if (clan == null) return;

        var query = "DELETE FROM novia.clan_settings WHERE `" + ClanSettingsProperty.CLAN_TAG.getValue() + "` = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(query)) {
            statement.setString(1, clan.getTag().toUpperCase());
            statement.executeUpdate();
            ClanPlugin.getInstance().getClanDatabase().deleteClan(clan);
            this.settingsCache.remove(clan.getTag().toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    Optional<SettingsObject> getClanSettings(String clanTag) {

        if (settingsCache.containsKey(clanTag)) {
            return settingsCache.get(clanTag);
        }

        var query = "SELECT * FROM novia.clan_settings " +
                "WHERE `" + ClanSettingsProperty.CLAN_TAG.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, clanTag);
            var resultSet = statement.executeQuery();

            if (!resultSet.next()) return Optional.empty();

            var settingsObject = new SettingsObject(
                    resultSet.getBoolean(ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue()),
                    resultSet.getBoolean(ClanSettingsProperty.CLAN_BANK_TOGGLED.getValue())
            );

            settingsCache.put(clanTag, Optional.of(settingsObject));

            return Optional.of(settingsObject);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }
}
