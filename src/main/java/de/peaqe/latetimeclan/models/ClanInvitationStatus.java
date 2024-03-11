package de.peaqe.latetimeclan.models;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 11.03.2024 | 18:16 Uhr
 * *
 */

public enum ClanInvitationStatus {
    OPEN("§aÖffentlich"),
    INVITATION("§eAuf Einladung"),
    CLOSED("§cGeschlossen");

    private final String status;

    ClanInvitationStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ClanInvitationStatus getFromStatus(String status) {
        for (var clanInvitationStatus : ClanInvitationStatus.values()) {
            if (clanInvitationStatus.getStatus().equals(status)) {
                return clanInvitationStatus;
            }
        }
        return null;
    }

}
