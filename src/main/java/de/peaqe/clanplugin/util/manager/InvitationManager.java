package de.peaqe.clanplugin.util.manager;

import de.peaqe.clanplugin.config.ClanInvitaionConfig;
import de.peaqe.clanplugin.objects.ClanInvitationStatus;
import de.peaqe.clanplugin.objects.ClanObject;

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

    public InvitationManager(ClanInvitaionConfig clanInvitaionConfig) {
        this.invitationCache = new InvitationCache(clanInvitaionConfig);
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

    public InvitationCache(ClanInvitaionConfig clanInvitaionConfig) {
        this.clanInvitaionConfig = clanInvitaionConfig;
    }

    public boolean invite(ClanObject clanObject, UUID uuid) {
        if (!this.isClanJoinable(clanObject)) return false;
        if (this.isInvited(uuid, clanObject)) return false;

        return this.clanInvitaionConfig.addInvitation(clanObject, uuid);
    }

    public void unInvite(UUID uuid, ClanObject clanObject) {
        if (!this.isInvited(uuid, clanObject)) return;
        this.clanInvitaionConfig.removeInvitation(clanObject, uuid);
    }

    public boolean isInvited(UUID uuid, ClanObject clanObject) {
        return this.clanInvitaionConfig.wasInvited(clanObject, uuid);
    }

    public boolean isClanJoinable(ClanObject clanObject) {
        if (clanObject.getMaxSize() <= clanObject.getMembers().size()) return false;
        return (!clanObject.getClanInvitationStatus().equals(ClanInvitationStatus.CLOSED));
    }

    public List<String> getInvitations(UUID uuid) {
        return this.clanInvitaionConfig.getClansWithInvitation(uuid);
    }

}
