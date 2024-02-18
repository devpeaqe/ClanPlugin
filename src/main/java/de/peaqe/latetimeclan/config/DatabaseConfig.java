package de.peaqe.latetimeclan.config;

import de.peaqe.latetimeclan.LateTimeClan;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 12:48 Uhr
 * *
 */

public class DatabaseConfig {

    private final LateTimeClan lateTimeClan;

    private final File file;
    private final FileConfiguration config;

    public DatabaseConfig(LateTimeClan lateTimeClan) {

        this.lateTimeClan = lateTimeClan;
        this.file = new File(this.lateTimeClan.getDataFolder().getAbsolutePath(), "database.yml");

        if (!this.file.exists()) {
            try {

                var bool = this.lateTimeClan.getDataFolder().mkdirs();
                var bool1 = this.file.createNewFile();

                if (bool && bool1) {

                    var config1 = YamlConfiguration.loadConfiguration(this.file);
                    config1 = YamlConfiguration.loadConfiguration(this.file);

                    config1.set("hostname", "localhost");
                    config1.set("username", "username");
                    config1.set("database", "latetime");
                    config1.set("password", "1234");
                    config1.set("port", 3306);
                    config1.save(this.file);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public String get(String key) {
        return this.config.getString(key);
    }

    public int getInt(String key) {
        return this.config.getInt(key);
    }

}
