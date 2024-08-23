package de.peaqe.clanplugin.listener.inventory.settings;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.navigation.ClanInfoPage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsChangeStatePage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsModerateChatPage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsToggleBankPage;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanSettingsPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanSettingsPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Einstellungen"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;

        switch (event.getSlot()) {

            case 29 -> {
                // Clan Status
                player.closeInventory();

                if (!clanPlayer.hasPermission(ClanAction.CHANGE_STATE)) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Du bist derzeit nicht berechtigt den %s zu ändern!",
                            "Clan-Status"
                    ));
                    return;
                }

                player.openInventory(new ClanSettingsChangeStatePage(this.clanPlugin, clanPlayer.getClan())
                        .getInventory());
            }

            case 31 -> {

                // Clan Chat
                player.closeInventory();

                if (!clanPlayer.hasPermission(ClanAction.SETTINGS_MODERATE_CHAT)) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt den %s %s zu schalten!",
                            "Clan-Status",
                            "§aein§8-/§caus"
                    ));
                    return;
                }

                player.openInventory(new ClanSettingsModerateChatPage(this.clanPlugin, clanPlayer.getClan())
                        .getInventory());

            }

            case 33 -> {
                player.closeInventory();

                if (!clanPlayer.hasPermission(ClanAction.SETTINGS_BANK_VIEW)) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt die %s %s zu stellen.!",
                            "Clan-Bank",
                            "§aein§8-/§caus"
                    ));
                    return;
                }

                player.openInventory(new ClanSettingsToggleBankPage(this.clanPlugin, clanPlayer.getClan())
                        .getInventory());
            }

            case 44 -> {
                player.closeInventory();
                player.openInventory(new ClanInfoPage(this.clanPlugin, clanPlayer.getClan()).getInventory(player));
            }

        }

    }
}
