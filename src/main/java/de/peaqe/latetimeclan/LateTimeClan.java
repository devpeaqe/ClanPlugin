package de.peaqe.latetimeclan;

import de.peaqe.latetimeclan.commands.ClanChatCommand;
import de.peaqe.latetimeclan.commands.ClanCommand;
import de.peaqe.latetimeclan.config.BlockedWordsConfig;
import de.peaqe.latetimeclan.config.DatabaseConfig;
import de.peaqe.latetimeclan.listener.connection.PlayerJoinListener;
import de.peaqe.latetimeclan.listener.connection.PlayerQuitListener;
import de.peaqe.latetimeclan.listener.inventory.ClanPageListener;
import de.peaqe.latetimeclan.listener.inventory.deletion.ClanDeleteConfirmPageListener;
import de.peaqe.latetimeclan.listener.inventory.member.*;
import de.peaqe.latetimeclan.listener.inventory.navigation.ClanInfoPageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsChangeStatePageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsModerateChatPageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsPageListener;
import de.peaqe.latetimeclan.listener.inventory.settings.ClanSettingsToggleBankPageListener;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.placeholder.ClanTagPlaceholder;
import de.peaqe.latetimeclan.provider.database.ClanDatabase;
import de.peaqe.latetimeclan.provider.database.ClanSettingsDatabase;
import de.peaqe.latetimeclan.provider.database.HeadDatabase;
import de.peaqe.latetimeclan.provider.database.PlayerDatabase;
import de.peaqe.latetimeclan.util.database.DatabaseConnection;
import de.peaqe.latetimeclan.util.manager.HeadManager;
import de.peaqe.latetimeclan.util.manager.InvitationManager;
import de.peaqe.latetimeclan.webhook.DiscordWebhook;
import de.peaqe.latetimeclan.webhook.WebhookSender;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class LateTimeClan extends JavaPlugin {

    private static LateTimeClan instance;

    private Economy economy;
    private Messages messages;
    private ClanDatabase clanDatabase;
    private HeadDatabase headDatabase;
    private InvitationManager invitationManager;
    private Map<UUID, ClanGroup> cache;
    private HeadManager headManager;
    private DatabaseConnection databaseConnection;
    private PlayerDatabase playerDatabase;
    //private DatabaseCache databaseCache;
    private WebhookSender webhookSender;
    private ClanSettingsDatabase clanSettingsDatabase;
    private BlockedWordsConfig blockedWordsConfig;

    @Override
    public void onEnable() {

        instance = this;

        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        var databaseConfig = new DatabaseConfig(this);

        this.databaseConnection = new DatabaseConnection(
                databaseConfig.get("hostname"),
                databaseConfig.get("username"),
                databaseConfig.get("password"),
                databaseConfig.get("database"),
                databaseConfig.getInt("port")
        );

        // Manager's
        this.registerManager();

        // Command's
        this.registerCommands();

        // Listener's
        this.registerListener();

        // Other
        new ClanTagPlaceholder();

        try {
            var embed = new DiscordWebhook.EmbedObject();
            embed.setColor(Color.GREEN);
            embed.addField("Status", "Das Clan-Plugin ist nun aktiviert!", true);

            var webhook = new DiscordWebhook();
            webhook.addEmbed(embed);
            webhook.execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        try {
            var embed = new DiscordWebhook.EmbedObject();
            embed.setColor(Color.RED);
            embed.addField("Status", "Das Clan-Plugin ist nun deaktiviert!", true);

            var webhook = new DiscordWebhook();
            webhook.addEmbed(embed);
            webhook.execute();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerManager() {
        this.messages = new Messages();

        this.clanDatabase =new ClanDatabase(this);
        this.clanSettingsDatabase = new ClanSettingsDatabase(this);
        this.headDatabase = new HeadDatabase(this);
        this.playerDatabase = new PlayerDatabase(this);
        this.headManager = new HeadManager(this);
        this.invitationManager = new InvitationManager();
        this.webhookSender = new WebhookSender();
        this.blockedWordsConfig = new BlockedWordsConfig(this);
    }

    private void registerListener() {
        new ClanInfoPageListener(this);
        new ClanMemberPageListener(this);
        new ClanMemberEditPageListener(this);
        new ClanMemberKickConfirmPageListener(this);
        new ClanMemberChangeGroupConfirmPageListener(this);
        new PlayerJoinListener(this);
        new ClanSettingsPageListener(this);
        new ClanSettingsChangeStatePageListener(this);
        new ClanSettingsModerateChatPageListener(this);
        new ClanSettingsToggleBankPageListener(this);
        new ClanDeleteConfirmPageListener(this);
        new PlayerQuitListener(this);
        new ClanPageListener(this);

        var clanMemberChangeGroupPageListener = new ClanMemberChangeGroupPageListener(this);
        cache = clanMemberChangeGroupPageListener.getCache();
    }

    private void registerCommands() {
        new ClanCommand(this);
        new ClanChatCommand(this);
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        var rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        this.economy = rsp.getProvider();
        return true;
    }


    public static LateTimeClan getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Messages getMessages() {
        return messages;
    }

    public ClanDatabase getClanDatabase() {
        return clanDatabase;
    }

    public HeadDatabase getHeadDatabase() {
        return headDatabase;
    }

    public InvitationManager getInvitationManager() {
        return invitationManager;
    }

    public Map<UUID, ClanGroup> getCache() {
        return cache;
    }

    public HeadManager getHeadManager() {
        return headManager;
    }

    public DatabaseConnection getDatabaseConnection() {
        return databaseConnection;
    }

    public PlayerDatabase getPlayerDatabase() {
        return playerDatabase;
    }

    public WebhookSender getWebhookSender() {
        return webhookSender;
    }

    public ClanSettingsDatabase getClanSettingsDatabase() {
        return clanSettingsDatabase;
    }

    public BlockedWordsConfig getBlockedWordsConfig() {
        return blockedWordsConfig;
    }
}
