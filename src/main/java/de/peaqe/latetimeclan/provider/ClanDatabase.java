package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.SettingsObject;
import de.peaqe.latetimeclan.objects.util.ClanDecoder;
import de.peaqe.latetimeclan.provider.util.ClanProperty;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private final ConcurrentMap<String, Optional<ClanObject>> clanCache;


    public ClanDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
        this.lateTimeClan = lateTimeClan;
        this.clanCache = new ConcurrentHashMap<>();
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

    public void createClan(ClanObject clanObject) {

        final var query = "INSERT INTO latetime.clan (" +
                ClanProperty.NAME.getValue() + ", " +
                ClanProperty.TAG.getValue() + ", " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + ", " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + ", " +
                ClanProperty.MAX_SIZE.getValue() + ", " +
                ClanProperty.MEMBERS.getValue() + ", " +
                ClanProperty.CLAN_BANK.getValue() +
                ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (clanObject == null || clanObject.getTag() == null) return;

        // Cache
        Optional<ClanObject> cachedClan = clanCache.computeIfAbsent(
                clanObject.getTag(), this::getClanFromDatabase);

        if (cachedClan.isPresent()) {
            if (Objects.equals(cachedClan.get(), clanObject)) return;
            this.updateClan(clanObject);
            return;
        }

        if (this.clanExists(clanObject.getTag())) {
            if (Objects.equals(this.getClan(clanObject.getTag()), Optional.of(clanObject))) return;
            this.updateClan(clanObject);
            return;
        }

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, clanObject.getName());
            statement.setString(2, clanObject.getTag().toLowerCase());
            statement.setString(3, clanObject.getClanFounderUUID());
            statement.setString(4, clanObject.getClanInvitationStatus().getStatus());
            statement.setInt(5, clanObject.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanObject.getMembers()));
            statement.setInt(7, clanObject.getClanBankAmount());
            this.lateTimeClan.getClanSettingsDatabase().insertClan(clanObject);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void deleteClan(ClanObject clan) {
        if (clan == null || clan.getTag() == null) return;

        var query = "DELETE FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(query)) {
            statement.setString(1, clan.getTag());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
            clanCache.remove(clan.getTag().toLowerCase());
        }
    }

    public Optional<ClanObject> getClan(String clanTag) {
        return clanCache.computeIfAbsent(clanTag.toLowerCase(), this::getClanFromDatabase);
    }

    public void updateClan(@NotNull ClanObject clanObject) {

        final var query = "UPDATE latetime.clan SET " +
                ClanProperty.NAME.getValue() + " = ?, " +
                ClanProperty.TAG.getValue() + " = ?, " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + " = ?, " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + " = ?, " +
                ClanProperty.MAX_SIZE.getValue() + " = ?, " +
                ClanProperty.MEMBERS.getValue() + " = ?, " +
                ClanProperty.CLAN_BANK.getValue() + " = ? " +
                "WHERE " + ClanProperty.TAG.getValue() + " = ?";

        // Cache
        Optional<ClanObject> cachedClan = clanCache.computeIfAbsent(
                clanObject.getTag(), tag -> Optional.of(clanObject));

        if (cachedClan.isEmpty()) {
            this.createClan(clanObject);
            return;
        }

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, clanObject.getName());
            statement.setString(2, clanObject.getTag().toLowerCase());
            statement.setString(3, clanObject.getClanFounderUUID());
            statement.setString(4, clanObject.getClanInvitationStatus().getStatus());
            statement.setInt(5, clanObject.getMaxSize());
            statement.setString(6, ClanDecoder.mapToString(clanObject.getMembers()));
            statement.setInt(7, clanObject.getClanBankAmount());
            statement.setString(8, clanObject.getTag());
            this.lateTimeClan.getClanSettingsDatabase().insertClan(clanObject);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public boolean clanExists(String clanTag) {

        if (this.getClan(clanTag).isPresent()) return true;

        this.connect();
        try {
            var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {

                var clanModel = new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet.getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
                );

                clanCache.put(clanTag.toLowerCase(), Optional.of(clanModel));

                return true;
            }

            return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public Optional<ClanObject> getClanFromDatabase(String clanTag) {

        this.connect();
        try {

            final var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toLowerCase());

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {

                var clanTag1 = resultSet.getString(ClanProperty.TAG.getValue());

                var clanModel = new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag1,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet.getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag1)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
                );

                return Optional.of(clanModel);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return Optional.empty();
    }

    @Nullable
    public ClanObject getClanModelByCondition(ClanProperty clanProperty, Object conditionItem) throws SQLException {

        var sql = "SELECT * FROM latetime.clan WHERE " + clanProperty.getValue() + " = ?";

        this.connect();

        var preparedStatement = this.getConnection().prepareStatement(sql);
        preparedStatement.setObject(1, conditionItem);

        var resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {

            var clanTag = resultSet.getString(ClanProperty.TAG.getValue());

            return new ClanObject(
                    resultSet.getString(ClanProperty.NAME.getValue()),
                    clanTag,
                    resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                    ClanInvitationStatus.getFromStatus(resultSet
                            .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                    resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                    ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                    this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag)
                            .orElse(new SettingsObject(true, false)),
                    resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
            );
        }

        return null;
    }

    public ClanObject getClanModelOfMember(UUID memberUUID) {

        var clanModel = new AtomicReference<>((ClanObject) null);
        var ignoreDatabase = new AtomicBoolean(false);

        this.clanCache.forEach((clanTag, optionalClan) -> {
            if (optionalClan.isPresent() && optionalClan.get().getMembers().containsKey(memberUUID)) {
                clanModel.set(optionalClan.get());
                ignoreDatabase.set(true);
            }
        });

        if (ignoreDatabase.get() && clanModel.get() != null) return clanModel.get();

        this.connect();
        try {

            var query = "SELECT * FROM latetime.clan WHERE " + ClanProperty.MEMBERS.getValue() + " LIKE ?";
            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, "%" + memberUUID.toString() + "%");

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {

                var clanTag = resultSet.getString(ClanProperty.TAG.getValue());

                clanModel.set(new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet
                                .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.lateTimeClan.getClanSettingsDatabase().getClanSettings(clanTag)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue())
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return clanModel.get();
    }
}
