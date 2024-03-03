package de.peaqe.latetimeclan;

import de.peaqe.latetimeclan.commands.ClanCommand;
import de.peaqe.latetimeclan.config.DatabaseConfig;
import de.peaqe.latetimeclan.listener.*;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.provider.ClanDatabase;
import de.peaqe.latetimeclan.test.TestCommand;
import de.peaqe.latetimeclan.util.manager.InvitationManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public final class LateTimeClan extends JavaPlugin {

    private static LateTimeClan instance;

    private Messages messages;
    private ClanDatabase clanDatabase;
    private DatabaseConfig databaseConfig;
    //private DatabaseCache databaseCache;
    private InvitationManager invitationManager;
    public Map<UUID, ClanGroupModel> cache;

    @Override
    public void onEnable() {

        instance = this;
        this.messages = new Messages();

        this.databaseConfig = new DatabaseConfig(this);

        this.clanDatabase = new ClanDatabase(
                this.databaseConfig.get("hostname"),
                this.databaseConfig.get("username"),
                this.databaseConfig.get("password"),
                this.databaseConfig.get("database"),
                this.databaseConfig.getInt("port")
        );
        //this.databaseCache = this.clanDatabase.getDatabaseCache();
        this.invitationManager = new InvitationManager();

        // Command's
        new TestCommand(this);
        new ClanCommand(this);

        // Listener's
        new ClanInfoPageListener(this);
        new ClanMemberPageListener(this);
        new ClanMemberEditPageListener(this);
        var clanMemberChangeGroupPageListener = new ClanMemberChangeGroupPageListener(this);
        new ClanMemberKickConfirmPageListener(this);
        new ClanMemberChangeGroupConfirmPageListener(this);

        cache = clanMemberChangeGroupPageListener.getCache();

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

    //public DatabaseCache getDatabaseCache() {
    //    return databaseCache;
    //}

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }

    public Map<UUID, ClanGroupModel> getCache() {
        return cache;
    }
}
