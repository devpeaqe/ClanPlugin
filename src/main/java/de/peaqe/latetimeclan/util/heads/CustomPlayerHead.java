package de.peaqe.latetimeclan.util.heads;

import com.destroystokyo.paper.profile.ProfileProperty;
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
 * @since 21.03.2024 | 23:45 Uhr
 * *
 */

public class CustomPlayerHead {

    public static ItemStack from(String value) {

        var head = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) head.getItemMeta();
        var profile = Bukkit.createProfile(UUID.randomUUID());

        profile.getProperties().add(new ProfileProperty("textures", value));
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);

        return head;
    }

}
