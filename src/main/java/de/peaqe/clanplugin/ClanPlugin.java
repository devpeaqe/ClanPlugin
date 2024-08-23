package de.peaqe.clanplugin;

import de.peaqe.clanplugin.commands.ClanChatCommand;
import de.peaqe.clanplugin.commands.ClanCommand;
import de.peaqe.clanplugin.config.BlockedWordsConfig;
import de.peaqe.clanplugin.config.ClanInvitaionConfig;
import de.peaqe.clanplugin.config.DatabaseConfig;
import de.peaqe.clanplugin.listener.connection.PlayerJoinListener;
import de.peaqe.clanplugin.listener.connection.PlayerQuitListener;
import de.peaqe.clanplugin.listener.inventory.ClanPageListener;
import de.peaqe.clanplugin.listener.inventory.deletion.ClanDeleteConfirmPageListener;
import de.peaqe.clanplugin.listener.inventory.member.*;
import de.peaqe.clanplugin.listener.inventory.navigation.ClanInfoPageListener;
import de.peaqe.clanplugin.listener.inventory.settings.ClanSettingsChangeStatePageListener;
import de.peaqe.clanplugin.listener.inventory.settings.ClanSettingsModerateChatPageListener;
import de.peaqe.clanplugin.listener.inventory.settings.ClanSettingsPageListener;
import de.peaqe.clanplugin.listener.inventory.settings.ClanSettingsToggleBankPageListener;
import de.peaqe.clanplugin.messages.Messages;
import de.peaqe.clanplugin.objects.ClanGroup;
import de.peaqe.clanplugin.placeholder.ClanTagPlaceholder;
import de.peaqe.clanplugin.provider.database.ClanDatabase;
import de.peaqe.clanplugin.provider.database.ClanSettingsDatabase;
import de.peaqe.clanplugin.provider.database.HeadDatabase;
import de.peaqe.clanplugin.provider.database.PlayerDatabase;
import de.peaqe.clanplugin.util.database.DatabaseConnection;
import de.peaqe.clanplugin.util.manager.HeadManager;
import de.peaqe.clanplugin.util.manager.InvitationManager;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import de.peaqe.clanplugin.webhook.WebhookSender;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Getter
public final class ClanPlugin extends JavaPlugin {

    @Getter
    private static ClanPlugin instance;

    private Economy economy;
    private Messages messages;
    private ClanDatabase clanDatabase;
    private HeadDatabase headDatabase;
    private InvitationManager invitationManager;
    private Map<UUID, ClanGroup> cache;
    private HeadManager headManager;
    private DatabaseConnection databaseConnection;
    private PlayerDatabase playerDatabase;
    private WebhookSender webhookSender;
    private ClanSettingsDatabase clanSettingsDatabase;
    private BlockedWordsConfig blockedWordsConfig;
    private ClanInvitaionConfig clanInvitaionConfig;

    @Override
    public void onEnable() {

        instance = this;

        var databaseConfig = new DatabaseConfig(this);
        this.clanInvitaionConfig = new ClanInvitaionConfig(this);

        this.databaseConnection = new DatabaseConnection(
                databaseConfig.get("hostname"),
                databaseConfig.get("username"),
                databaseConfig.get("password"),
                databaseConfig.get("database"),
                databaseConfig.getInt("port")
        );

        if (!this.setupEconomy()) {
            this.getServer().getConsoleSender().sendMessage("Clanplugin deactivated");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

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
        this.invitationManager = new InvitationManager(this.clanInvitaionConfig);
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
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) return false;

        var rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;

        this.economy = rsp.getProvider();
        return true;
    }
}
