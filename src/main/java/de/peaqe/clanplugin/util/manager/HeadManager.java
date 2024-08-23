package de.peaqe.clanplugin.util.manager;

import de.peaqe.clanplugin.ClanPlugin;
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
 * @since 05.03.2024 | 16:04 Uhr
 * *
 */

public class HeadManager {

    private final ClanPlugin clanPlugin;

    public HeadManager(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
    }

    public ItemStack registerHead(UUID playerUniqueId) {

        var head = new ItemStack(Material.PLAYER_HEAD);
        var headMeta = (SkullMeta) head.getItemMeta();

        var target = Bukkit.getOfflinePlayerIfCached(UniqueIdManager.getName(playerUniqueId));
        if (target == null) target = Bukkit.getOfflinePlayer(playerUniqueId);
        if (!target.hasPlayedBefore()) return null;

        headMeta.setOwningPlayer(target);
        head.setItemMeta(headMeta);

        this.clanPlugin.getHeadDatabase().insertHead(
                target.getName(),
                target.getUniqueId(),
                head
        );

        return head;
    }

}
