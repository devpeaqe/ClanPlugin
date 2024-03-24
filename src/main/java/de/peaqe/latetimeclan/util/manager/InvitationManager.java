package de.peaqe.latetimeclan.util.manager;

import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;

import java.util.*;

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
        return this.invitationCache.invite(uuid, clanObject);
    }

    public void unInvite(UUID uuid, ClanObject clanObject) {
        this.invitationCache.unInvite(uuid, clanObject);
    }

    public boolean isInvited(UUID uuid, String clanTag) {
        return this.invitationCache.isInvited(uuid, clanTag);
    }

    public boolean isClanJoinable(ClanObject clanObject) {
        return this.invitationCache.isClanJoinable(clanObject);
    }

    public List<String> getInvitations(UUID uuid) {
        return this.invitationCache.getInvitations().get(uuid);
    }

}

class InvitationCache {

    private final Map<UUID, List<String>> invitations;

    public InvitationCache() {
        this.invitations = new HashMap<>();
    }

    public boolean invite(UUID uuid, ClanObject clanObject) {

        var clanInvitations = this.invitations.get(uuid);
        if (clanInvitations == null) clanInvitations = new ArrayList<>();

        if (!this.isClanJoinable(clanObject)) return false;
        if (this.isInvited(uuid, clanObject.getTag())) return false;
        clanInvitations.add(clanObject.getTag());

        this.invitations.put(uuid, clanInvitations);
        return true;
    }

    public void unInvite(UUID uuid, ClanObject clanObject) {

        var clanInvitations = this.invitations.get(uuid);
        if (clanInvitations == null) clanInvitations = new ArrayList<>();

        if (!this.isInvited(uuid, clanObject.getTag())) return;
        clanInvitations.remove(clanObject.getTag());

        this.invitations.put(uuid, clanInvitations);
    }

    public boolean isInvited(UUID uuid, String clanTag) {
        System.out.println(this.invitations);
        if (this.invitations.get(uuid) == null || this.invitations.get(uuid).isEmpty()) return false;
        return this.invitations.get(uuid).contains(clanTag);
    }

    public boolean isClanJoinable(ClanObject clanObject) {
        if (clanObject.getMaxSize() <= clanObject.getMembers().size()) return false;
        return (!clanObject.getClanInvitationStatus().equals(ClanInvitationStatus.CLOSED));
    }

    public Map<UUID, List<String>> getInvitations() {
        return invitations;
    }
}
