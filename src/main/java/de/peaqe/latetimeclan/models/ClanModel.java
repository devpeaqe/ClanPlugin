package de.peaqe.latetimeclan.models;

import de.peaqe.latetimeclan.LateTimeClan;

import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:25 Uhr
 * *
 */

public class ClanModel {

    private String name, tag, clanFounderUuid, clanInvitationStatus;
    private int maxSize;
    private Map<UUID, ClanGroupModel> members;

    public ClanModel(String name, String tag, String clanFounderUuid, String clanInvitationStatus, int maxSize, Map<UUID, ClanGroupModel> members) {
        this.name = name;
        this.tag = tag;
        this.clanFounderUuid = clanFounderUuid;
        this.clanInvitationStatus = clanInvitationStatus;
        this.maxSize = maxSize;
        this.members = members;
    }

    public ClanModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getClanFounderUUID() {
        return clanFounderUuid;
    }

    public void setClanFounderUUID(String clanFounderUuid) {
        this.clanFounderUuid = clanFounderUuid;
    }

    public String getClanInvitationStatus() {
        return clanInvitationStatus;
    }

    public void setClanInvitationStatus(String clanInvitationStatus) {
        this.clanInvitationStatus = clanInvitationStatus;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Map<UUID, ClanGroupModel> getMembers() {
        return members;
    }

    public void setMembers(Map<UUID, ClanGroupModel> members) {
        this.members = members;
    }

    public void kick(ClanPlayer clanPlayer) {

        var currentMembers = this.getMembers();

        currentMembers.remove(clanPlayer.getUniqueId());
        this.setMembers(currentMembers);

        // TODO: Add cache
        LateTimeClan.getInstance().getClanDatabase().updateClan(this);
        clanPlayer.reload();

    }

}
