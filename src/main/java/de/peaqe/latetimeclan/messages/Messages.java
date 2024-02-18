package de.peaqe.latetimeclan.messages;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 14.02.2024 | 16:55 Uhr
 * *
 */

public class Messages {

    private final String prefix;
    private final String colorNormal;

    public Messages() {
        this.prefix = "§8» §9Clan §8┃";
        this.colorNormal = "§7";
    }

    public String compileMessage(String message, String... highlights) {
        if (message == null || highlights == null) {
            throw new IllegalArgumentException("Message and highlights cannot be null.");
        }

        String[] parts = message.split("%s", -1);
        StringBuilder result = new StringBuilder(parts[0]);

        int minLen = Math.min(parts.length - 1, highlights.length);
        for (int i = 0; i < minLen; i++) {
            result.append(getColorHighlight(i))
                    .append(highlights[i])
                    .append(colorNormal)
                    .append(parts[i + 1]);
        }

        if (highlights.length < parts.length - 1) {
            for (int i = minLen; i < parts.length - 1; i++) {
                result.append(parts[i + 1]);
            }
        }

        return result.toString();
    }

    private String getColorHighlight(int index) {
        return index % 2 == 0 ? "§e" : "§6";
    }

}

