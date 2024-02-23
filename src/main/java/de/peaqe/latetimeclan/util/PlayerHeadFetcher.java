package de.peaqe.latetimeclan.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public static String toBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();

            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();

            return item;

        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

}
