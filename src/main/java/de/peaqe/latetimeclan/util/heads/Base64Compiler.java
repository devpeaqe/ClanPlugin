package de.peaqe.latetimeclan.util.heads;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.provider.util.HeadProperty;
import de.peaqe.latetimeclan.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

/**
 *
 * Base64Compiler fetches player heads as ItemStacks and provides methods to serialize and deserialize them.
 *
 * @author peaqe
 * @version 1.0
 * @since 21.02.2024 | 19:40 Uhr
 *
 */
public class Base64Compiler {

    public static String toBase64(ItemStack itemStack) {
        try {
            var outputStream = new ByteArrayOutputStream();
            var dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(itemStack);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stack.", e);
        }
    }

    public static ItemStack fromBase64(String base64) {
        try {
            var data = Base64.getDecoder().decode(base64);
            var inputStream = new ByteArrayInputStream(data);
            var dataInput = new BukkitObjectInputStream(inputStream);
            var itemStack = (ItemStack) dataInput.readObject();
            dataInput.close();
            if (itemStack == null)
                return new ItemBuilder(Material.PLAYER_HEAD)
                        .setDisplayName("§cUnable to load.")
                        .build();
            return itemStack;
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            return new ItemBuilder(Material.PLAYER_HEAD)
                    .setDisplayName("§cUnable to load.")
                    .build();
        }
    }


    public static ItemStack getPlayerHeadFromUUID(UUID clanOwnerUUID) {
        return LateTimeClan.getInstance().getHeadDatabase().getHead(HeadProperty.UUID, clanOwnerUUID.toString());
    }
}
