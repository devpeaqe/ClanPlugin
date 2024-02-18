package de.peaqe.latetimeclan;

import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.provider.ClanDatabase;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class LateTimeClan extends JavaPlugin {

    private Messages messages;
    private ClanDatabase clanDatabase;

    @Override
    public void onEnable() {
        this.messages = new Messages();
        this.clanDatabase = new ClanDatabase("", "", "", "", 3306);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
