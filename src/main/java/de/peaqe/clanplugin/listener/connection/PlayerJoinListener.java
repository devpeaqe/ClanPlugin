package de.peaqe.clanplugin.listener.connection;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
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

    private final ClanPlugin clanPlugin;

    public PlayerJoinListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        var player = event.getPlayer();
        this.clanPlugin.getPlayerDatabase().registerPlayer(player);

        var playerSkull = new ItemStack(Material.PLAYER_HEAD);
        var playerSkullMeta = (SkullMeta) playerSkull.getItemMeta();

        playerSkullMeta.setOwningPlayer(player);
        playerSkull.setItemMeta(playerSkullMeta);

        this.clanPlugin.getHeadDatabase().insertHead(player.getName(), player.getUniqueId(), playerSkull);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;
        clanPlayer.getClan().sendNotification(
                "§8[§a+§8] %s",
                player.getName()
        );

    }
}
