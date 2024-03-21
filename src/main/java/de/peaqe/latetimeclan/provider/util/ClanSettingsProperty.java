package de.peaqe.latetimeclan.provider.util;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:12 Uhr
 * *
 */

public enum ClanSettingsProperty {

    CLAN_TAG("clan-tag"),
    CLAN_CHAT_TOGGLED("clan-chat-toggled"),
    CLAN_BANK_TOGGLED("clan-bank-toggled");

    private final String value;

    ClanSettingsProperty(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
