package de.peaqe.latetimeclan.models;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:15 Uhr
 * *
 */

public class Settings {

    private boolean clanChatToggled;

    public Settings(boolean clanChatToggled) {
        this.clanChatToggled = clanChatToggled;
    }

    public boolean isClanChatToggled() {
        return clanChatToggled;
    }

    public void setClanChatToggled(boolean clanChatToggled) {
        this.clanChatToggled = clanChatToggled;
    }
}
