package de.peaqe.latetimeclan.models.util;

import de.peaqe.latetimeclan.models.ClanPlayer;
import org.bukkit.entity.Player;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:31 Uhr
 * *
 */

public class ClanPermissionHandler {

    public boolean hasPermission(Player player, ClanAction clanAction) {
        final var clanPlayer = ClanPlayer.fromPlayer(player);
        return clanPlayer.hasPermission(clanAction);
    }

}
