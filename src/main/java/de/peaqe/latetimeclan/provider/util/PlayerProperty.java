package de.peaqe.latetimeclan.provider.util;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 14:02 Uhr
 * *
 */

public enum PlayerProperty {

    NAME("name"),
    UUID("uniqueId"),
    LAST_SEEN("lastSeen");

    private final String value;

    PlayerProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
