package de.peaqe.latetimeclan.config;

import de.peaqe.latetimeclan.LateTimeClan;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 12:48 Uhr
 * *
 */

public class BlockedWordsConfig {

    private final LateTimeClan lateTimeClan;

    private final File file;
    private final FileConfiguration config;

    public BlockedWordsConfig(LateTimeClan lateTimeClan) {

        this.lateTimeClan = lateTimeClan;
        this.file = new File(this.lateTimeClan.getDataFolder().getAbsolutePath(), "blocked-words.yml");

        if (!this.file.exists()) {
            try {

                var bool = this.lateTimeClan.getDataFolder().mkdirs();
                var bool1 = this.file.createNewFile();

                if (bool1) {
                    var config1 = YamlConfiguration.loadConfiguration(this.file);

                    config1 = YamlConfiguration.loadConfiguration(this.file);
                    config1.set("words", List.of("nigger", "niger"));
                    config1.save(this.file);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public List<String> getBlockedWords() {
        return this.config.getStringList("words");
    }

}
