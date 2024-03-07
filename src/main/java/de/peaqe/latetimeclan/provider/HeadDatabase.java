package de.peaqe.latetimeclan.provider;

import de.peaqe.latetimeclan.provider.util.HeadProperty;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:17 Uhr
 * *
 */

public class HeadDatabase {

    private final String hostname, username, password, database;
    private final int port;
    private Connection connection;
    private final Map<UUID, String> headCache;

    public HeadDatabase(String hostname, String username, String password, String database, int port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;

        this.createTableIfNotExists();
        this.headCache = new HashMap<>();
    }

    public void createTableIfNotExists() {
        this.connect();
        try {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS latetime.heads (" +
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

    public void close() {
        try {
            Bukkit.getConsoleSender().sendMessage("§bHEADS §7»» §cConnection Closed");
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        try {
            Bukkit.getConsoleSender().sendMessage("§bHEADS §7»» §aConnection Opened");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + hostname + ":" + port + "/" + database,
                    username,
                    password
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertHead(String name, UUID uuid, ItemStack itemStack) {

        System.out.println(1);

        var headBase64 = Base64Compiler.toBase64(itemStack);
        if (this.headCache.containsValue(headBase64) || this.headCache.containsKey(uuid)) return;

        System.out.println(2);

        var query = "INSERT INTO latetime.heads (`" +
                HeadProperty.NAME.getValue() + "`, `" +
                HeadProperty.UUID.getValue() + "`, `" +
                HeadProperty.HEAD.getValue() +
                "`) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `" +
                HeadProperty.NAME.getValue() + "` = VALUES(`" + HeadProperty.NAME.getValue() + "`);";

        this.connect();
        try (var statement = this.connection.prepareStatement(query)) {

            System.out.println(3);
            statement.setString(1, name.toLowerCase());
            statement.setString(2, uuid.toString());
            statement.setBytes(3, Base64.getDecoder().decode(headBase64));

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(4);
                this.headCache.put(uuid, headBase64);
            }

        } catch (SQLException e) {
            System.out.println(5);
            Bukkit.getConsoleSender().sendMessage("§cError executing SQL query: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            System.out.println(6);
            this.close();
        }
        System.out.println(7);
    }

    @Nullable
    public ItemStack getHead(@NotNull HeadProperty headProperty, @NotNull String string) {

        if (string.isEmpty()) return null;

        if (headProperty.equals(HeadProperty.NAME)) string = string.toLowerCase();
        if (headProperty.equals(HeadProperty.HEAD) && this.headCache.containsValue(string)) {
            return Base64Compiler.fromBase64(this.headCache.get(UUID.fromString(string)));
        }

        if (headProperty.equals(HeadProperty.UUID) && this.headCache.containsKey(UUID.fromString(string))) {
            return Base64Compiler.fromBase64(this.headCache.get(UUID.fromString(string)));
        }

        var query = "SELECT `" + headProperty.getValue() + "` FROM latetime.heads WHERE `" + headProperty.getValue() + "` = ?";

        this.connect();
        try {

            var statement = this.connection.prepareStatement(query);

            statement.setString(1, string);

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {

                if (headProperty.equals(HeadProperty.HEAD)) {
                    var headBase64 = this.convertBlobToString(resultSet.getBlob(headProperty.getValue()));
                    this.headCache.put(UUID.fromString(string), headBase64);
                    return Base64Compiler.fromBase64(headBase64);
                }

                var headBase64 = resultSet.getString(headProperty.getValue());
                this.headCache.put(UUID.fromString(string), headBase64);
                return Base64Compiler.fromBase64(headBase64);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.close();
        }

        return null;
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

            return outputStream.toString();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}