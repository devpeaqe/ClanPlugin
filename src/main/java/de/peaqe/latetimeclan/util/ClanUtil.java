package de.peaqe.latetimeclan.util;

import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 12.03.2024 | 11:25 Uhr
 * *
 */

public class ClanUtil {

    public static ClanInvitationStatus getClanInvitationStatus(ClanModel clanModel) {
        if (clanModel.getMembers().size() >= clanModel.getMaxSize()) return ClanInvitationStatus.CLOSED;
        return clanModel.getClanInvitationStatus();
    }

    public static boolean isPermitted(ClanPlayer sender, ClanPlayer target, ClanAction clanAction) {
        return (sender.hasPermission(clanAction) &&
                sender.getClanGroup().getPermissionLevel() > target.getClanGroup().getPermissionLevel());

    }

}
