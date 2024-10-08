package de.peaqe.clanplugin.provider.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:20 Uhr
 * *
 */

@Getter
@AllArgsConstructor
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

}
