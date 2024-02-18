package de.peaqe.latetimeclan.models;

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
    private Map<ClanGroupModel, UUID> members;

    public ClanModel(String name, String tag, String clanFounderUuid, String clanInvitationStatus, int maxSize, Map<ClanGroupModel, UUID> members) {
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

    public Map<ClanGroupModel, UUID> getMembers() {
        return members;
    }

    public void setMembers(Map<ClanGroupModel, UUID> members) {
        this.members = members;
    }
}
