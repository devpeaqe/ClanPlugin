package de.peaqe.latetimeclan.util.manager;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.config.ClanInvitaionConfig;
import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;

import java.util.List;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 03.03.2024 | 09:49 Uhr
 * *
 */

public class InvitationManager {

    private final InvitationCache invitationCache;

    public InvitationManager() {
        this.invitationCache = new InvitationCache();
    }

    public boolean invite(UUID uuid, ClanObject clanObject) {
        return this.invitationCache.invite(clanObject, uuid);
    }

    public void unInvite(UUID uuid, ClanObject clanObject) {
        this.invitationCache.unInvite(uuid, clanObject);
    }

    public boolean isInvited(UUID uuid, ClanObject clanObject) {
        return this.invitationCache.isInvited(uuid, clanObject);
    }

    public boolean isClanJoinable(ClanObject clanObject) {
        return this.invitationCache.isClanJoinable(clanObject);
    }

    public List<String> getInvitations(UUID uuid) {
        return this.invitationCache.getInvitations(uuid);
    }

}

class InvitationCache {

    private final ClanInvitaionConfig clanInvitaionConfig;

    public InvitationCache() {
        this.clanInvitaionConfig = LateTimeClan.getInstance().getClanInvitaionConfig();
    }

    public boolean invite(ClanObject clanObject, UUID uuid) {

        var invitationList = this.clanInvitaionConfig.getPlayerInvitedFrom(clanObject);

        if (!this.isClanJoinable(clanObject)) return false;
        if (this.isInvited(uuid, clanObject)) return false;

        this.clanInvitaionConfig.addInvitation(clanObject, uuid);
        return true;
    }

    public void unInvite(UUID uuid, ClanObject clanObject) {

        var invitationList = this.clanInvitaionConfig.getPlayerInvitedFrom(clanObject);

        if (!this.isInvited(uuid, clanObject)) return;
        this.clanInvitaionConfig.removeInvitation(clanObject, uuid);
    }

    public boolean isInvited(UUID uuid, ClanObject clanObject) {
        var invitationList = this.clanInvitaionConfig.getPlayerInvitedFrom(clanObject);
        return invitationList.contains(uuid);
    }

    public boolean isClanJoinable(ClanObject clanObject) {
        if (clanObject.getMaxSize() <= clanObject.getMembers().size()) return false;
        return (!clanObject.getClanInvitationStatus().equals(ClanInvitationStatus.CLOSED));
    }

    // TODO: Filter to get all clans the player was invited from
    public List<String> getInvitations(UUID uuid) {
        return this.clanInvitaionConfig.getClansWithInvitation(uuid);
    }

}
