package de.peaqe.latetimeclan.provider.util;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:20 Uhr
 * *
 */

public enum ClanProperty {

    NAME("name"),
    TAG("tag"),
    CLAN_INVITATION_STATUS("clan_invitation_status"),
    CLAN_FOUNDER_UUID("clan_founder_uuid"),
    CLAN_COLOR("clan_color"),
    CREATE_TIMESTAMP("date_created"),
    MAX_SIZE("max_size"),
    MEMBERS("members"),
    CLAN_BANK("clan_bank");

    private final String value;

    ClanProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
