package de.peaqe.clanplugin.listener.inventory.navigation;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.member.ClanMemberPage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsPage;
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

public class ClanInfoPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanInfoPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Informationen"
                ))
        )) return;

        event.setCancelled(true);
        var slot = event.getSlot();

        switch (slot) {
            case 29 -> {
                // 20 » Statics
            }

            case 31 -> {

                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) {
                    player.closeInventory();
                    return;
                }

                var clan = clanPlayer.getClan();

                if (clanPlayer.hasPermission(ClanAction.OPEN_SETTINGS)) {
                    player.closeInventory();
                    player.openInventory(new ClanSettingsPage(this.clanPlugin, clan).getInventory());
                }

            }

            case 33 -> {
                // 23 » Clan Member
                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) {
                    player.closeInventory();
                    return;
                }

                var clan = clanPlayer.getClan();

                player.closeInventory();
                player.openInventory(new ClanMemberPage(this.clanPlugin, clan).getInventory());
            }
        }

    }

}
