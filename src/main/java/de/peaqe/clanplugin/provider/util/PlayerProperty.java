package de.peaqe.clanplugin.provider.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 14:02 Uhr
 * *
 */

@Getter
@AllArgsConstructor
public enum PlayerProperty {

    NAME("name"),
    UUID("uniqueId"),
    LAST_SEEN("lastSeen");

    private final String value;

}
