package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.util.ClanDecoder;
import de.peaqe.latetimeclan.provider.util.ClanProperty;

import javax.annotation.Nullable;
import java.sql.SQLException;
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

public class ClanDatabase extends DatabaseProvider {

    private final LateTimeClan lateTimeClan;

    public ClanDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
        this.lateTimeClan = lateTimeClan;
        this.createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.clan (" +
                    "  `" + ClanProperty.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_FOUNDER_UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_INVITATION_STATUS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.MAX_SIZE.getValue() + "` INT NOT NULL," +
                    "  `" + ClanProperty.MEMBERS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_BANK.getValue() + "` INT NOT NULL," +
                    "  PRIMARY KEY (`tag`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void createClan(ClanModel clanModel) {

        final var query = "INSERT INTO latetime.clan (" +
                ClanProperty.NAME.getValue() + ", " +
                ClanProperty.TAG.getValue() + ", " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + ", " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + ", " +
                ClanProperty.MAX_SIZE.getValue() + ", " +
                ClanProperty.MEMBERS.getValue() +
                ClanProperty.CLAN_BANK.getValue() +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (clanModel == null || clanModel.getTag() == null) return;

        if (this.clanExists(clanModel.getTag())) {
            if (Objects.equals(this.getClan(clanModel.getTag()), clanModel)) return;
            this.updateClan(clanModel);
            return;
        }

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag().toLowerCase());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus().getStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));
            statement.setInt(7, clanModel.getClanBankAmount());
            this.lateTimeClan.getClanSettingsDatabase().insertClan(clanModel);

            //this.simpleCache.cache(clanModel);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void updateClan(ClanModel clanModel) {

        final var query = "UPDATE latetime.clan SET " +
                ClanProperty.NAME.getValue() + " = ?, " +
                ClanProperty.TAG.getValue() + " = ?, " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + " = ?, " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + " = ?, " +
                ClanProperty.MAX_SIZE.getValue() + " = ?, " +
                ClanProperty.MEMBERS.getValue() + " = ? " +
                ClanProperty.CLAN_BANK.getValue() + " = ? " +
                "WHERE " + ClanProperty.TAG.getValue() + " = ?";

        if (this.getClan(clanModel.getTag()) == null) {
            this.createClan(clanModel);
            return;
        }

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, clanModel.getName());
            statement.setString(2, clanModel.getTag().toLowerCase());
            statement.setString(3, clanModel.getClanFounderUUID());
            statement.setString(4, clanModel.getClanInvitationStatus().getStatus());
            statement.setInt(5, clanModel.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanModel.getMembers()));
            statement.setInt(7, clanModel.getClanBankAmount());
            statement.setString(8, clanModel.getTag());
            this.lateTimeClan.getClanSettingsDatabase().insertClan(clanModel);

            //this.simpleCache.remove(clanModel.getTag());
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
            var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                //var clanModel = new ClanModel(
                //        resultSet.getString(ClanProperty.NAME.getValue()),
                //        resultSet.getString(ClanProperty.TAG.getValue()),
                //        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                //        resultSet.getString(ClanProperty.CLAN_INVITATION_STATUS.getValue()),
                //        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                //        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue()))
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
            final var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {

                var clanTag1 = resultSet.getString(ClanProperty.TAG.getValue());

                var clanModel = new ClanModel(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag1,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet
                                .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag1),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
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

    @Nullable
    public ClanModel getClanModelByCondition(ClanProperty clanProperty, Object conditionItem) throws SQLException {

        var sql = "SELECT * FROM latetime.clan WHERE " + clanProperty.getValue() + " = ?";

        this.connect();

        var preparedStatement = this.getConnection().prepareStatement(sql);
        preparedStatement.setObject(1, conditionItem);

        var resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {

            var clanTag = resultSet.getString(ClanProperty.TAG.getValue());

            var clanModel = new ClanModel(
                    resultSet.getString(ClanProperty.NAME.getValue()),
                    clanTag,
                    resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                    ClanInvitationStatus.getFromStatus(resultSet
                            .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                    resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                    ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                    this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag),
                    resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
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
            var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue()));
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

            var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.MEMBERS.getValue() + " LIKE ?";
            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, "%" + memberUUID.toString() + "%");

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {

                var clanTag = resultSet.getString(ClanProperty.TAG.getValue());

                clanModel = new ClanModel(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet
                                .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
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
