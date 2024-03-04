package de.peaqe.latetimeclan.provider.util;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:18 Uhr
 * *
 */

public enum HeadProperty {

    NAME("name"),
    UUID("uniqueId"),
    HEAD("base64");

    private final String value;

    HeadProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
