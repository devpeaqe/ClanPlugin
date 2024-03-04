package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.ClanMemberEditPage;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.util.uuid.UUIDFetcher;
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

        var currentItemPlayerName = currentItem.getItemMeta().getDisplayName().split("§8• §a")[1];
        var currentClanPlayer = ClanPlayer.fromPlayer(UUIDFetcher.getUUID(currentItemPlayerName));

        var clanPlayer = ClanPlayer.fromPlayer(player);

        player.closeInventory();
        player.openInventory(new ClanMemberEditPage(this.lateTimeClan, clanPlayer.getClan())
                .getInventory(clanPlayer, currentClanPlayer));

    }

}
