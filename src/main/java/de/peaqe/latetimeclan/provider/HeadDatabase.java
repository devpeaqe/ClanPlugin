package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.util.HeadProperty;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:17 Uhr
 * *
 */

public class HeadDatabase extends DatabaseProvider {

    public HeadDatabase(LateTimeClan lateTimeClan) {
        super(lateTimeClan);
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
        //if (this.headCache.containsValue(headBase64) || this.headCache.containsKey(uuid)) return;

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

        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage("§cError executing SQL query: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            this.close();
        }
    }

    @Nullable
    public ItemStack getHead(@NotNull HeadProperty headProperty, @NotNull String string) {

        var placeHolder = new ItemBuilder(Material.PLAYER_HEAD).setDisplayName("§cUnable to load.").build();
        if (string.isEmpty()) return placeHolder;

        if (headProperty.equals(HeadProperty.NAME)) string = string.toLowerCase();
        //if (headProperty.equals(HeadProperty.HEAD) && this.headCache.containsValue(string)) {
        //    return Base64Compiler.fromBase64(this.headCache.get(UUID.fromString(string)));
        //}

        //if (headProperty.equals(HeadProperty.UUID) && this.headCache.containsKey(UUID.fromString(string))) {
        //    return Base64Compiler.fromBase64(this.headCache.get(UUID.fromString(string)));
        //}

        var query = "SELECT `" + HeadProperty.HEAD.getValue() + "` FROM latetime.heads WHERE `" +
                headProperty.getValue() + "` = ?";

        this.connect();
        try {

            var statement = this.getConnection().prepareStatement(query);

            statement.setString(1, string);

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                var headBase64 = this.convertBlobToString(resultSet.getBlob(HeadProperty.HEAD.getValue()));
                return Base64Compiler.fromBase64(headBase64);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return placeHolder;
    }

    public boolean headExists(HeadProperty headProperty, String string) {
        return this.getHead(headProperty, string) != null;
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