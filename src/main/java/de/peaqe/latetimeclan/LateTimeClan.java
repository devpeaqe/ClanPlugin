package de.peaqe.latetimeclan;

import de.peaqe.latetimeclan.commands.ClanChatCommand;
import de.peaqe.latetimeclan.commands.ClanCommand;
import de.peaqe.latetimeclan.config.DatabaseConfig;
import de.peaqe.latetimeclan.listener.connection.PlayerJoinListener;
import de.peaqe.latetimeclan.listener.inventory.member.*;
import de.peaqe.latetimeclan.listener.inventory.navigation.ClanInfoPageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsChangeStatePageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsPageListener;
import de.peaqe.latetimeclan.listener.plugin.PluginDisableListener;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.provider.ClanDatabase;
import de.peaqe.latetimeclan.provider.HeadDatabase;
import de.peaqe.latetimeclan.util.database.DatabaseConnection;
import de.peaqe.latetimeclan.util.manager.HeadManager;
import de.peaqe.latetimeclan.util.manager.InvitationManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class LateTimeClan extends JavaPlugin {

    private static LateTimeClan instance;

    private Messages messages;
    private ClanDatabase clanDatabase;
    private HeadDatabase headDatabase;
    private DatabaseConfig databaseConfig;
    private InvitationManager invitationManager;
    private Map<UUID, ClanGroupModel> cache;
    private HeadManager headManager;
    private DatabaseConnection databaseConnection;
    //private DatabaseCache databaseCache;

    @Override
    public void onEnable() {

        instance = this;

        // Manager's
        this.registerManager();

        // Command's
        this.registerCommands();

        // Listener's
        this.registerListener();

    }

    private void registerManager() {

        this.messages = new Messages();
        this.databaseConfig = new DatabaseConfig(this);
        this.headManager = new HeadManager(this);
        this.invitationManager = new InvitationManager();

        this.databaseConnection = new DatabaseConnection(
                this.databaseConfig.get("hostname"),
                this.databaseConfig.get("username"),
                this.databaseConfig.get("password"),
                this.databaseConfig.get("database"),
                this.databaseConfig.getInt("port")
        );

        //this.databaseCache = this.clanDatabase.getDatabaseCache();
    }

    private void registerListener() {

        new ClanInfoPageListener(this);
        new ClanMemberPageListener(this);
        new ClanMemberEditPageListener(this);
        new ClanMemberKickConfirmPageListener(this);
        new ClanMemberChangeGroupConfirmPageListener(this);
        new PlayerJoinListener(this);
        new ClanSettingsPageListener(this);
        new PluginDisableListener(this);
        new ClanSettingsChangeStatePageListener(this);

        var clanMemberChangeGroupPageListener = new ClanMemberChangeGroupPageListener(this);
        cache = clanMemberChangeGroupPageListener.getCache();
    }

    private void registerCommands() {
        new ClanCommand(this);
        new ClanChatCommand(this);
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

    //public DatabaseCache getDatabaseCache() {
    //    return databaseCache;
    //}

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }

    public Map<UUID, ClanGroupModel> getCache() {
        return cache;
    }

    public HeadDatabase getHeadDatabase() {
        return headDatabase;
    }

    public HeadManager getHeadManager() {
        return headManager;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }
}
