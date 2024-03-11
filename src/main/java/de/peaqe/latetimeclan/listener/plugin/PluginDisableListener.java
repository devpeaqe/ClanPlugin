package de.peaqe.latetimeclan.listener.plugin;

import de.peaqe.latetimeclan.LateTimeClan;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 10.03.2024 | 18:35 Uhr
 * *
 */

public class PluginDisableListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public PluginDisableListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        //this.lateTimeClan.getHeadDatabase().getHeadCache().clear();
    }

}
