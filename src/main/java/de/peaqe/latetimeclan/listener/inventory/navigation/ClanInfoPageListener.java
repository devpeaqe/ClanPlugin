package de.peaqe.latetimeclan.listener.inventory.navigation;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberPage;
import de.peaqe.latetimeclan.inventory.settings.ClanSettingsPage;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
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

    private final LateTimeClan lateTimeClan;

    public ClanInfoPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
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

                player.closeInventory();

                if (clanPlayer.hasPermission(ClanAction.OPEN_SETTINGS)) {
                    player.openInventory(new ClanSettingsPage(this.lateTimeClan, clan).getInventory());
                    return;
                }

                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Du hast nicht die benötigte Berechtigung um die %s zu öffnen!",
                        "Einstellungen"
                ));

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
                player.openInventory(new ClanMemberPage(this.lateTimeClan, clan).getInventory());
            }
        }

    }

}
