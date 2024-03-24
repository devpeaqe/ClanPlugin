package de.peaqe.latetimeclan.listener.inventory;

import de.peaqe.latetimeclan.LateTimeClan;
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

public class ClanPageListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public ClanPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getOriginalTitle().startsWith(this.lateTimeClan.getMessages()
                .compileMessage(""))) return;
        if (event.getCurrentItem() == null) {
        }

    }
}
