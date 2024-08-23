package de.peaqe.clanplugin.provider.database;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanInvitationStatus;
import de.peaqe.clanplugin.objects.ClanObject;
import de.peaqe.clanplugin.objects.SettingsObject;
import de.peaqe.clanplugin.objects.util.ClanDecoder;
import de.peaqe.clanplugin.provider.DatabaseProvider;
import de.peaqe.clanplugin.provider.util.ClanProperty;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
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

    private final ClanPlugin clanPlugin;
    private final ConcurrentMap<String, Optional<ClanObject>> clanCache;

    public ClanDatabase(ClanPlugin clanPlugin) {
        super(clanPlugin);
        this.clanPlugin = clanPlugin;
        this.clanCache = new ConcurrentHashMap<>();
        this.createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS novia.clan (" +
                    "  `" + ClanProperty.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.TAG.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_FOUNDER_UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_COLOR.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_INVITATION_STATUS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.MAX_SIZE.getValue() + "` INT NOT NULL," +
                    "  `" + ClanProperty.MEMBERS.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + ClanProperty.CLAN_BANK.getValue() + "` INT NOT NULL," +
                    "  `" + ClanProperty.CREATE_TIMESTAMP.getValue() + "` TIMESTAMP NOT NULL," +
                    "  PRIMARY KEY (`tag`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void createClan(ClanObject clanObject) {

        final var query = "INSERT INTO novia.clan (" +
                ClanProperty.NAME.getValue() + ", " +
                ClanProperty.TAG.getValue() + ", " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + ", " +
                ClanProperty.CLAN_COLOR.getValue() + ", " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + ", " +
                ClanProperty.MAX_SIZE.getValue() + ", " +
                ClanProperty.MEMBERS.getValue() + ", " +
                ClanProperty.CLAN_BANK.getValue() + ", " +
                ClanProperty.CREATE_TIMESTAMP.getValue() + ") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            statement.setString(2, clanObject.getTag().toUpperCase());
            statement.setString(3, clanObject.getClanFounderUUID());
            statement.setString(4, clanObject.getColor());
            statement.setString(5, clanObject.getClanInvitationStatus().getStatus());
            statement.setInt(6, clanObject.getMaxSize());
            statement.setString(7, ClanDecoder.mapToString(clanObject.getMembers()));
            statement.setInt(8, clanObject.getClanBankAmount());
            statement.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()),
                    Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")));
            //statement.setString(9, clanObject.getTag());
            this.clanPlugin.getClanSettingsDatabase().insertClan(clanObject);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void deleteClan(ClanObject clan) {
        if (clan == null || clan.getTag() == null) return;

        var query = "DELETE FROM novia.clan WHERE `" + ClanProperty.TAG.getValue() + "` = ?";
        this.connect();

        try (var statement = this.getConnection().prepareStatement(query)) {
            statement.setString(1, clan.getTag());
            statement.executeUpdate();
            this.clanCache.remove(clan.getTag().toUpperCase());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public Optional<ClanObject> getClan(String clanTag) {
        return clanCache.computeIfAbsent(clanTag.toUpperCase(), this::getClanFromDatabase);
    }

    public void updateClan(@NotNull ClanObject clanObject) {

        final var query = "UPDATE novia.clan SET " +
                ClanProperty.NAME.getValue() + " = ?, " +
                ClanProperty.TAG.getValue() + " = ?, " +
                ClanProperty.CLAN_FOUNDER_UUID.getValue() + " = ?, " +
                ClanProperty.CLAN_COLOR.getValue() + " = ?, " +
                ClanProperty.CLAN_INVITATION_STATUS.getValue() + " = ?, " +
                ClanProperty.MAX_SIZE.getValue() + " = ?, " +
                ClanProperty.MEMBERS.getValue() + " = ?, " +
                ClanProperty.CLAN_BANK.getValue() + " = ?, " +
                ClanProperty.CREATE_TIMESTAMP.getValue() + " = ? " +
                "WHERE " + ClanProperty.TAG.getValue() + " = ?";

        // Cache
        Optional<ClanObject> cachedClan = clanCache.computeIfAbsent(
                clanObject.getTag(), tag -> Optional.of(clanObject));

        if (cachedClan.isEmpty()) {
            this.createClan(clanObject);
            return;
        }

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, clanObject.getName());
            statement.setString(2, clanObject.getTag().toUpperCase());
            statement.setString(3, clanObject.getClanFounderUUID());
            statement.setString(4, clanObject.getColor());
            statement.setString(5, clanObject.getClanInvitationStatus().getStatus());
            statement.setInt(6, clanObject.getMaxSize());
            statement.setString(7, ClanDecoder.mapToString(clanObject.getMembers()));
            statement.setInt(8, clanObject.getClanBankAmount());
            statement.setTimestamp(9, new Timestamp(clanObject.getDateCreated().getTime()));
            statement.setString(10, clanObject.getTag());
            this.clanPlugin.getClanSettingsDatabase().insertClan(clanObject);

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
            var query = "SELECT * FROM novia.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toUpperCase());

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {

                var clanModel = new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        resultSet.getString(ClanProperty.CLAN_COLOR.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet.getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.clanPlugin.getClanSettingsDatabase().getClanSettings(clanTag)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue()),
                        resultSet.getTimestamp(ClanProperty.CREATE_TIMESTAMP.getValue())
                );

                clanCache.put(clanTag.toUpperCase(), Optional.of(clanModel));

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

            final var query = "SELECT * FROM novia.clan WHERE " + ClanProperty.TAG.getValue() + " = ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, clanTag.toUpperCase());

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {

                var clanTag1 = resultSet.getString(ClanProperty.TAG.getValue());

                var clanModel = new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag1,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        resultSet.getString(ClanProperty.CLAN_COLOR.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet.getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.clanPlugin.getClanSettingsDatabase().getClanSettings(clanTag1)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue()),
                        resultSet.getTimestamp(ClanProperty.CREATE_TIMESTAMP.getValue())
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

    public ClanObject getClanModelOfMember(UUID memberUUID) {
        var clanModel = new AtomicReference<>((ClanObject) null);
        var ignoreDatabase = new AtomicBoolean(false);

        this.clanCache.forEach((clanTag, optionalClan) -> {
            if (optionalClan.isPresent() && optionalClan.get().getMembers().containsKey(memberUUID)) {
                clanModel.set(optionalClan.get());
                ignoreDatabase.set(true);
            }
        });

        if (ignoreDatabase.get() && clanModel.get() != null) {
            return clanModel.get();
        }

        this.connect();
        try {
            var query = "SELECT * FROM novia.clan WHERE " + ClanProperty.MEMBERS.getValue() + " LIKE ?";
            var statement = this.getConnection().prepareStatement(query);
            statement.setString(1, "%" + memberUUID.toString() + "%");

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var clanTag = resultSet.getString(ClanProperty.TAG.getValue());
                clanModel.set(new ClanObject(
                        resultSet.getString(ClanProperty.NAME.getValue()),
                        clanTag,
                        resultSet.getString(ClanProperty.CLAN_FOUNDER_UUID.getValue()),
                        resultSet.getString(ClanProperty.CLAN_COLOR.getValue()),
                        ClanInvitationStatus.getFromStatus(resultSet
                                .getString(ClanProperty.CLAN_INVITATION_STATUS.getValue())),
                        resultSet.getInt(ClanProperty.MAX_SIZE.getValue()),
                        ClanDecoder.stringToMap(resultSet.getString(ClanProperty.MEMBERS.getValue())),
                        this.clanPlugin.getClanSettingsDatabase().getClanSettings(clanTag)
                                .orElse(new SettingsObject(true, false)),
                        resultSet.getInt(ClanProperty.CLAN_BANK.getValue()),
                        resultSet.getTimestamp(ClanProperty.CREATE_TIMESTAMP.getValue())
                ));
                this.clanCache.put(clanTag.toUpperCase(), Optional.of(clanModel.get()));
            } else {
                this.clanCache.entrySet().removeIf(entry -> entry.getValue().isPresent());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return clanModel.get();
    }
}
