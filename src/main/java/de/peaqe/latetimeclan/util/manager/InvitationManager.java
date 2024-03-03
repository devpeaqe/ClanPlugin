package de.peaqe.latetimeclan.util.manager;

import de.peaqe.latetimeclan.models.ClanModel;

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

    public boolean invite(UUID uuid, ClanModel clanModel) {
        return this.invitationCache.invite(uuid, clanModel);
    }

    public void unInvite(UUID uuid, ClanModel clanModel) {
        this.invitationCache.unInvite(uuid, clanModel);
    }

    public boolean isInvited(UUID uuid, String clanTag) {
        return this.invitationCache.isInvited(uuid, clanTag);
    }

    public boolean isClanJoinable(ClanModel clanModel) {
        return this.invitationCache.isClanJoinable(clanModel);
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

    public InvitationCache(Map<UUID, List<String>> invitations) {
        this.invitations = invitations;
    }

    public boolean invite(UUID uuid, ClanModel clanModel) {

        var clanInvitations = this.invitations.get(uuid);
        if (clanInvitations == null) clanInvitations = new ArrayList<>();

        if (!this.isClanJoinable(clanModel)) return false;
        if (this.isInvited(uuid, clanModel.getTag())) return false;
        clanInvitations.add(clanModel.getTag());

        this.invitations.put(uuid, clanInvitations);
        return true;
    }

    public void unInvite(UUID uuid, ClanModel clanModel) {

        var clanInvitations = this.invitations.get(uuid);
        if (clanInvitations == null) clanInvitations = new ArrayList<>();

        if (!this.isInvited(uuid, clanModel.getTag())) return;
        clanInvitations.remove(clanModel.getTag());

        this.invitations.put(uuid, clanInvitations);
    }

    public boolean isInvited(UUID uuid, String clanTag) {
        System.out.println(this.invitations);
        if (this.invitations.get(uuid) == null || this.invitations.get(uuid).isEmpty()) return false;
        return this.invitations.get(uuid).contains(clanTag);
    }

    public boolean isClanJoinable(ClanModel clanModel) {
        if (clanModel.getMaxSize() <= clanModel.getMembers().size()) return false;
        return (!clanModel.getClanInvitationStatus().equalsIgnoreCase("closed"));
    }

    public Map<UUID, List<String>> getInvitations() {
        return invitations;
    }
}
