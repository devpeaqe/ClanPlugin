package de.peaqe.latetimeclan.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 21.02.2024 | 19:40 Uhr
 * *
 */

public class PlayerHeadFetcher {

    public static ItemStack getPlayerHeadFromUUID(UUID playerUUID) {

        var playerHead = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) playerHead.getItemMeta();

        if (meta != null) {

            var targetPlayer = Bukkit.getOfflinePlayer(playerUUID);
            if (!targetPlayer.hasPlayedBefore()) {
                targetPlayer = Bukkit.getOfflinePlayer("peaqe");
            }

            meta.setOwningPlayer(targetPlayer);
            playerHead.setItemMeta(meta);
        }

        return playerHead;
    }

}
