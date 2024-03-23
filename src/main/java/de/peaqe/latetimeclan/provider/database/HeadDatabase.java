package de.peaqe.latetimeclan.provider.database;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.DatabaseProvider;
import de.peaqe.latetimeclan.provider.util.HeadProperty;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:17 Uhr
 * *
 */

public class HeadDatabase extends DatabaseProvider {

    private final ConcurrentMap<String, Optional<ItemStack>> headCache;

    public HeadDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
        this.headCache = new ConcurrentHashMap<>();
        this.createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        this.connect();
        try {
            this.getConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.heads (" +
                    "  `" + HeadProperty.NAME.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + HeadProperty.UUID.getValue() + "` VARCHAR(255) NOT NULL," +
                    "  `" + HeadProperty.HEAD.getValue() + "` BLOB NOT NULL," +
                    "  PRIMARY KEY (`" + HeadProperty.UUID.getValue() + "`)" +
                    ")");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public void insertHead(String name, UUID uuid, ItemStack itemStack) {

        var headBase64 = Base64Compiler.toBase64(itemStack);
        if (this.headCache.containsKey(uuid.toString())) return;

        var query = "INSERT INTO latetime.heads (`" +
                HeadProperty.NAME.getValue() + "`, `" +
                HeadProperty.UUID.getValue() + "`, `" +
                HeadProperty.HEAD.getValue() +
                "`) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `" +
                HeadProperty.NAME.getValue() + "` = VALUES(`" + HeadProperty.NAME.getValue() + "`);";

        this.connect();
        try (var statement = this.getConnection().prepareStatement(query)) {

            statement.setString(1, name.toLowerCase());
            statement.setString(2, uuid.toString());
            statement.setBytes(3, Base64.getDecoder().decode(headBase64));

            statement.executeUpdate();

            // Update cache after successful insertion
            headCache.put(uuid.toString(), Optional.of(itemStack));

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§cError executing SQL query: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    public Optional<ItemStack> getHead(@NotNull HeadProperty headProperty, @NotNull String string) {

        var placeHolder = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§cUnable to load.").build();
        if (string.isEmpty()) return Optional.of(placeHolder);

        if (headProperty.equals(HeadProperty.NAME)) string = string.toLowerCase();
        if (headProperty.equals(HeadProperty.HEAD) && this.headCache.containsKey(string)) {
            return this.headCache.get(string);
        }

        if (headProperty.equals(HeadProperty.UUID) && this.headCache.containsKey(string)) {
            return this.headCache.get(string);
        }

        var query = "SELECT `" + HeadProperty.HEAD.getValue() + "` FROM latetime.heads WHERE `" +
                headProperty.getValue() + "` = ?";

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, string);

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var headBase64 = this.convertBlobToString(resultSet.getBlob(HeadProperty.HEAD.getValue()));
                var headItemStack = Base64Compiler.fromBase64(headBase64);
                this.headCache.put(string, Optional.of(headItemStack));
                return Optional.of(headItemStack);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return Optional.of(placeHolder);
    }

    private String convertBlobToString(Blob blob) {

        if (blob == null) {
            return null;
        }

        try (InputStream inputStream = blob.getBinaryStream()) {

            var outputStream = new ByteArrayOutputStream();
            var buffer = new byte[4096];
            var bytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
