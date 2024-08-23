package de.peaqe.clanplugin.config;

import de.peaqe.clanplugin.ClanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 12:48 Uhr
 * *
 */

public class DatabaseConfig {

    private final FileConfiguration config;

    public DatabaseConfig(ClanPlugin clanPlugin) {

        File file = new File(clanPlugin.getDataFolder().getAbsolutePath(), "database.yml");

        if (!file.exists()) {
            try {

                var bool = clanPlugin.getDataFolder().mkdirs();
                var bool1 = file.createNewFile();

                if (bool) {
                    Bukkit.getLogger().log(Level.INFO,
                            "Created Folder: " + clanPlugin.getDataFolder().getAbsolutePath());
                }

                if (bool1) {

                    var config1 = YamlConfiguration.loadConfiguration(file);
                    config1 = YamlConfiguration.loadConfiguration(file);

                    config1.set("hostname", "localhost");
                    config1.set("username", "username");
                    config1.set("database", "novia");
                    config1.set("password", "1234");
                    config1.set("port", 3306);
                    config1.save(file);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String key) {
        return this.config.getString(key);
    }

    public int getInt(String key) {
        return this.config.getInt(key);
    }

}
