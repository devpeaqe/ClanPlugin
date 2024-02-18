package de.peaqe.latetimeclan.models.util;

import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:31 Uhr
 * *
 */

@Getter
public enum ClanAction {

    DELETE(3),
    CHANGE_COLOR(3),
    PROMOTE(2),
    DEMOTE(2),
    INVITE(1),
    KICK(1),
    CHAT(0);

    private final int permissionLevel;

    ClanAction(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}
