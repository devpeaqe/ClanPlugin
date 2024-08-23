package de.peaqe.clanplugin.listener.connection;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:35 Uhr
 * *
 */

public class PlayerQuitListener implements Listener {

    private final ClanPlugin clanPlugin;

    public PlayerQuitListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var player = event.getPlayer();

        // Update Datetime
        if (!event.getReason().equals(PlayerQuitEvent.QuitReason.ERRONEOUS_STATE)) {
            this.clanPlugin.getPlayerDatabase().registerPlayer(player);
        }

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;
        clanPlayer.getClan().sendNotification(
                "§8[§c-§8] %s",
                player.getName()
        );
    }

}
