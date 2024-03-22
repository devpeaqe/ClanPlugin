package de.peaqe.latetimeclan.util.heads;

import org.bukkit.inventory.ItemStack;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 19:34 Uhr
 * *
 */

@SuppressWarnings(value = "unused")
public enum Head {

    RED_BUTTON("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmU" +
            "vOGU1ZDcyNWRkOGJmZjI0MDczOGU4NWRiYWRhZWVjYTU0MmQ2ODAwYTc4MDIzOTM4ZjBmMjljY2JiZmNhOGQ2NiJ9fX0="),
    KING("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk0Mz" +
            "UyZDkwYWEwYjE3OTkzNTkzZTJiNTY0NDI3ZWZjNDQwZWMzNjQwZTdjYmE5OTk0ZmJhMDNiMDNjMzAifX19"),
    BATTERY("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTU4" +
            "NDQzMmFmNmYzODIxNjcxMjAyNThkMWVlZThjODdjNmU3NWQ5ZTQ3OWU3YjBkNGM3YjZhZDQ4Y2ZlZWYifX19"),
    PIGGY_BANK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM" +
            "Tk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19"),
    GO_BACK("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE4" +
            "YTJkZDViZWYwYjA3M2IxMzI3MWE3ZWViOWNmZWE3YWZlODU5M2M1N2E5MzgyMWU0MzE3NTU3MjQ2MTgxMiJ9fX0="),
    NERD("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc5ODZ" +
            "hNDU4NGYyNmQzYWQ2YzNhYjk0Y2QyYWQ1ZjgwYTdkMWM2NzE2YWQyN2Q1YWM3YjE0NDg2YTI3MDJkNiJ9fX0="),
    PAPER_PEN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYT" +
            "dlZDY2ZjVhNzAyMDlkODIxMTY3ZDE1NmZkYmMwY2EzYmYxMWFkNTRlZDVkODZlNzVjMjY1ZjdlNTAyOWVjMSJ9fX0="),
    SETTINGS("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZT" +
            "RkNDliYWU5NWM3OTBjM2IxZmY1YjJmMDEwNTJhNzE0ZDYxODU0ODFkNWIxYzg1OTMwYjNmOTlkMjMyMTY3NCJ9fX0="),
    EDIT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYmM" +
            "5N2Y3MTgzY2RlMmQ5OWU5YWJlYjJiMjQxZDU2ZWViYTE4MGM0M2UyMzUzODNlYTRlYWRmMDgzNTYyMyJ9fX0="),;

    private final String base64;

    Head(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public static ItemStack compile(Head head) {
        return CustomPlayerHead.from(head.getBase64());
    }

}
