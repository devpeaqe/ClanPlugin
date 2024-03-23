package de.peaqe.latetimeclan.util.color;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.03.2024 | 19:36 Uhr
 * *
 */

public class Hex {

    public static String color(String hex) {
        if (hex.length() != 6) return hex;

        StringBuilder formattedHex = new StringBuilder("§x"); // Füge §x am Anfang hinzu
        for (int i = 0; i < hex.length(); i += 2) {
            formattedHex.append("§").append(hex.charAt(i)).append("§").append(hex.charAt(i + 1));
        }

        return formattedHex.toString();
    }

}
