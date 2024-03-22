package de.peaqe.latetimeclan.listener.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberEditPage;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.util.manager.UniqueIdManager;
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

public class ClanMemberPageListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public ClanMemberPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder"
                ))
        )) return;

        event.setCancelled(true);

        var currentItem = event.getCurrentItem();
        if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;

        var clanPlayer = ClanPlayerObject.fromPlayer(player);
        if (clanPlayer == null) return;

        if (event.getSlot() == 44) {
            player.closeInventory();
            player.openInventory(new ClanInfoPage(this.lateTimeClan, clanPlayer.getClan())
                    .getInventory(player));
            return;
        }

        if (!currentItem.getItemMeta().getDisplayName().contains("§8• §a")) return;
        var currentItemPlayerName = currentItem.getItemMeta().getDisplayName().split("§8• §a")[1];
        if (currentItemPlayerName == null) return;

        var currentClanPlayer = ClanPlayerObject.fromPlayer(UniqueIdManager.getUUID(currentItemPlayerName));

        player.closeInventory();
        player.openInventory(new ClanMemberEditPage(this.lateTimeClan, clanPlayer.getClan())
                .getInventory(clanPlayer, currentClanPlayer));

    }

}
