package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.util.HeadProperty;
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

        var playerSkull = new ItemStack(Material.PLAYER_HEAD);
        var playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();

        playerSkullMeta.setOwningPlayer(player);
        playerSkull.setItemMeta(playerSkullMeta);

        this.lateTimeClan.getHeadDatabase().insertHead(player.getName(), player.getUniqueId(), playerSkull);

        /**
         * @TEST: HEAD
         */
        var headItem = this.lateTimeClan.getHeadDatabase().getHead(HeadProperty.UUID, player.getUniqueId().toString());

        if (headItem == null) {
            player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                    "§cEs ist ein Fehler aufgetreten!"
            ));
            return;
        }

        player.getInventory().addItem(headItem);

        // TODO: Clan notify
    }

}
