package de.peaqe.latetimeclan.listener.inventory.navigation;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberPage;
import de.peaqe.latetimeclan.inventory.settings.ClanSettingsPage;
import de.peaqe.latetimeclan.models.ClanPlayer;
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
            case 20 -> {
                // 20 » Statics
            }

            case 22 -> {

                var clanPlayer = ClanPlayer.fromPlayer(player);
                if (clanPlayer == null) {
                    player.closeInventory();
                    return;
                }

                var clan = clanPlayer.getClan();

                player.closeInventory();
                player.openInventory(new ClanSettingsPage(this.lateTimeClan, clan).getInventory());

            }

            case 24 -> {
                // 23 » Clan Member
                var clanPlayer = ClanPlayer.fromPlayer(player);
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
