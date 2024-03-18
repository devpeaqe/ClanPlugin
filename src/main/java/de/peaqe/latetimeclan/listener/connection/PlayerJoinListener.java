package de.peaqe.latetimeclan.listener.connection;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        this.lateTimeClan.getPlayerDatabase().registerPlayer(player);

        var playerSkull = new ItemStack(Material.PLAYER_HEAD);
        var playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();

        playerSkullMeta.setOwningPlayer(player);
        playerSkull.setItemMeta(playerSkullMeta);

        this.lateTimeClan.getHeadDatabase().insertHead(player.getName(), player.getUniqueId(), playerSkull);

        var clanPlayer = ClanPlayer.fromPlayer(player);

        if (clanPlayer == null) return;
        clanPlayer.getClan().sendNotification(
                "§8[§a+§8] %s",
                player.getName()
        );

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var player = event.getPlayer();
        var clanPlayer = ClanPlayer.fromPlayer(player);

        if (clanPlayer == null) return;
        clanPlayer.getClan().sendNotification(
                "§8[§c-§8] %s",
                player.getName()
        );
    }

}
