package de.peaqe.latetimeclan.objects.util;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:31 Uhr
 * *
 */

public enum ClanAction {

    DELETE(3),
    CHANGE_COLOR(3),
    BANK_REMOVE(3),
    CHANGE_GROUP(2),
    PROMOTE(2),
    DEMOTE(2),
    CHANGE_STATE(2),
    OPEN_SETTINGS(2),
    INVITE(1),
    KICK(1),
    SETTINGS_MODERATE_CHAT(1),
    BANK_ADD(0),
    CHAT(0);

    private final int permissionLevel;

    ClanAction(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }
}
