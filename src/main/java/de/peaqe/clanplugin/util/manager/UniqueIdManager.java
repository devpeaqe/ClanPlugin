package de.peaqe.clanplugin.util.manager;

import de.peaqe.clanplugin.ClanPlugin;

import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.02.2024 | 21:16 Uhr
 * *
 */

public class UniqueIdManager {

    public static UUID getUUID(String name) {
        return ClanPlugin.getInstance().getPlayerDatabase().getUniqueId(name).orElse(null);
    }

    public static String getName(UUID playerUniqueId) {
        return ClanPlugin.getInstance().getPlayerDatabase().getName(playerUniqueId);
    }
}
