package de.peaqe.latetimeclan.util.manager;

import de.peaqe.latetimeclan.LateTimeClan;
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

    private final LateTimeClan lateTimeClan;

    public HeadManager(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
    }

    public ItemStack registerHead(String playerName) {

        var head = new ItemStack(Material.PLAYER_HEAD);
        var headMeta = (SkullMeta) head.getItemMeta();

        var target = Bukkit.getOfflinePlayerIfCached(playerName);
        if (target == null) target = Bukkit.getOfflinePlayer(playerName);

        headMeta.setOwningPlayer(target);
        head.setItemMeta(headMeta);

        this.lateTimeClan.getHeadDatabase().insertHead(
                target.getName(),
                target.getUniqueId(),
                head
        );

        return head;
    }

    public ItemStack registerHead(UUID playerUniqueId) {

        var head = new ItemStack(Material.PLAYER_HEAD);
        var headMeta = (SkullMeta) head.getItemMeta();

        var target = Bukkit.getOfflinePlayerIfCached(UniqueIdManager.getName(playerUniqueId));
        if (target == null) target = Bukkit.getOfflinePlayer(playerUniqueId);
        if (!target.hasPlayedBefore()) return null;

        headMeta.setOwningPlayer(target);
        head.setItemMeta(headMeta);

        this.lateTimeClan.getHeadDatabase().insertHead(
                target.getName(),
                target.getUniqueId(),
                head
        );

        return head;
    }

}
