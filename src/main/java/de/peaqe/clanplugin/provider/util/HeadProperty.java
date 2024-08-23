package de.peaqe.clanplugin.provider.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 04.03.2024 | 17:18 Uhr
 * *
 */

@Getter
@AllArgsConstructor
public enum HeadProperty {

    NAME("name"),
    UUID("uniqueId"),
    HEAD("base64");

    private final String value;
}
