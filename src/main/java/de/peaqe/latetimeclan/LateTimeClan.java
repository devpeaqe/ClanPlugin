package de.peaqe.latetimeclan;

import de.peaqe.latetimeclan.config.DatabaseConfig;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.provider.ClanDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public final class LateTimeClan extends JavaPlugin {

    private static LateTimeClan instance;

    private Messages messages;
    private ClanDatabase clanDatabase;
    private DatabaseConfig databaseConfig;

    @Override
    public void onEnable() {

        instance = this;
        this.messages = new Messages();

        this.databaseConfig = new DatabaseConfig(this);

        this.clanDatabase = new ClanDatabase(
                this.databaseConfig.get("hostname"),
                this.databaseConfig.get("username"),
                this.databaseConfig.get("database"),
                this.databaseConfig.get("password"),
                this.databaseConfig.getInt("port")
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static LateTimeClan getInstance() {
        return instance;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public Messages getMessages() {
        return messages;
    }

    public ClanDatabase getClanDatabase() {
        return clanDatabase;
    }
}
