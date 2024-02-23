package de.peaqe.latetimeclan.util.heads;

import de.peaqe.latetimeclan.util.PlayerHeadFetcher;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 19:34 Uhr
 * *
 */

public enum Head {

    RED_BUTTON("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU1ZDcyNWRkOGJmZjI0MDczOGU4NWRiYWRhZWVjYTU0MmQ2ODAwYTc4MDIzOTM4ZjBmMjljY2JiZmNhOGQ2NiJ9fX0="),
    EDIT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYmM5N2Y3MTgzY2RlMmQ5OWU5YWJlYjJiMjQxZDU2ZWViYTE4MGM0M2UyMzUzODNlYTRlYWRmMDgzNTYyMyJ9fX0="),


    ;

    private final String base64;

    Head(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    @Nullable
    public static ItemStack compile(Head head) {

        var itemStack = (ItemStack) null;

        try {
            itemStack = PlayerHeadFetcher.fromBase64(head.getBase64());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return itemStack;
    }

}
