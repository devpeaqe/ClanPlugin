package de.peaqe.latetimeclan.listener.connection;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
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

    private final LateTimeClan lateTimeClan;

    public PlayerQuitListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        var player = event.getPlayer();

        // Update Datetime
        if (!event.getReason().equals(PlayerQuitEvent.QuitReason.ERRONEOUS_STATE)) {
            this.lateTimeClan.getPlayerDatabase().registerPlayer(player);
        }

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;
        clanPlayer.getClan().sendNotification(
                "§8[§c-§8] %s",
                player.getName()
        );
    }

}
