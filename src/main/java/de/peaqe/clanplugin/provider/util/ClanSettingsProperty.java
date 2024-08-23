package de.peaqe.clanplugin.provider.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:12 Uhr
 * *
 */

@Getter
@AllArgsConstructor
public enum ClanSettingsProperty {

    CLAN_TAG("clan-tag"),
    CLAN_CHAT_TOGGLED("clan-chat-toggled"),
    CLAN_BANK_TOGGLED("clan-bank-toggled");

    private final String value;
}
