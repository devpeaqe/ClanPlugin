package de.peaqe.latetimeclan.util.manager;

import de.peaqe.latetimeclan.LateTimeClan;

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
        return LateTimeClan.getInstance().getPlayerDatabase().getUniqueId(name);
    }

    public static String getName(UUID playerUniqueId) {
        return LateTimeClan.getInstance().getPlayerDatabase().getName(playerUniqueId);
    }
}
