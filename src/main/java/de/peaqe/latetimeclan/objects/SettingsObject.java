package de.peaqe.latetimeclan.objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.03.2024 | 12:15 Uhr
 * *
 */

public class SettingsObject {

    private boolean clanChatToggled;
    private boolean clanBankToggled;

    public SettingsObject(boolean clanChatToggled, boolean clanBankToggled) {
        this.clanChatToggled = clanChatToggled;
        this.clanBankToggled = clanBankToggled;
    }

    public boolean isClanChatToggled() {
        return clanChatToggled;
    }

    public void setClanChatToggled(boolean clanChatToggled) {
        this.clanChatToggled = clanChatToggled;
    }

    public boolean isClanBankToggled() {
        return clanBankToggled;
    }

    public void setClanBankToggled(boolean clanBankToggled) {
        this.clanBankToggled = clanBankToggled;
    }
}
