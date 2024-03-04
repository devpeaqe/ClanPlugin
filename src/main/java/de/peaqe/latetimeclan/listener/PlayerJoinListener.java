package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:35 Uhr
 * *
 */

public class PlayerJoinListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public PlayerJoinListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        var player = event.getPlayer();

        // Instert player head into database

        var playerSkull = new ItemStack(Material.PLAYER_HEAD);
        var playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();

        playerSkullMeta.setOwningPlayer(player);
        playerSkull.setItemMeta(playerSkullMeta);

        this.lateTimeClan.getHeadDatabase().insertHead(player.getName(), player.getUniqueId(), playerSkull);

        // TODO: Clan notify
    }

}
