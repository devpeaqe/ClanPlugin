package de.peaqe.clanplugin.config;

import de.peaqe.clanplugin.ClanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 12:48 Uhr
 * *
 */

public class BlockedWordsConfig {

    private final FileConfiguration config;

    public BlockedWordsConfig(ClanPlugin clanPlugin) {

        File file = new File(clanPlugin.getDataFolder().getAbsolutePath(), "blocked-words.yml");

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
                    config1.set("words", List.of("nigger", "niger"));
                    config1.save(file);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public List<String> getBlockedWords() {
        return this.config.getStringList("words");
    }

}
