package de.peaqe.clanplugin.listener.inventory;

import de.peaqe.clanplugin.ClanPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getView().getOriginalTitle().startsWith(this.clanPlugin.getMessages()
                .compileMessage(""))) return;
        if (event.getCurrentItem() == null) return;

        Bukkit.getLogger().log(Level.INFO, "Clicked: " + event.getCurrentItem().getType());

    }
}
