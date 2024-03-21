package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.Settings;
import de.peaqe.latetimeclan.provider.util.ClanSettingsProperty;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:11 Uhr
 * *
 */

public class ClanSettingsDatabase extends DatabaseProvider {

    public ClanSettingsDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
        this.createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.clan_settings (" +
                    "  `" + ClanSettingsProperty.CLAN_TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue() + "` BOOLEAN NOT NULL," +
                    "  PRIMARY KEY (`" + ClanSettingsProperty.CLAN_TAG.getValue() + "`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    void insertClan(@NotNull ClanModel clanModel) {
        var settings = clanModel.getSettings();
        if (settings == null) return;

        var query = "INSERT INTO latetime.clan_settings VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `" + ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue() + "` = ?," +
                "``" + ClanSettingsProperty.CLAN_BANK_TOGGLED.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, clanModel.getTag());
            statement.setBoolean(2, clanModel.getSettings().isClanChatToggled());
            statement.setBoolean(3, clanModel.getSettings().isClanBankToggled());

            statement.setBoolean(4, clanModel.getSettings().isClanChatToggled());
            statement.setBoolean(5, clanModel.getSettings().isClanBankToggled());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    Settings getClanSettings(String clanTag) {
        var query = "SELECT `" + ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue() + "` FROM latetime.clan_settings " +
                "WHERE `" + ClanSettingsProperty.CLAN_TAG.getValue() + "` = ?";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, clanTag);
            var resultSet = statement.executeQuery();

            if (!resultSet.next()) return null;
            return new Settings(
                    resultSet.getBoolean(ClanSettingsProperty.CLAN_CHAT_TOGGLED.getValue()),
                    resultSet.getBoolean(ClanSettingsProperty.CLAN_BANK_TOGGLED.getValue())
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

}
