package de.peaqe.latetimeclan.models;

import de.peaqe.latetimeclan.LateTimeClan;
import org.bukkit.Bukkit;

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

    private String name, tag, clanFounderUuid;
    private ClanInvitationStatus clanInvitationStatus;
    private int maxSize;
    private Map<UUID, ClanGroupModel> members;

    public ClanModel(String name, String tag, String clanFounderUuid, ClanInvitationStatus clanInvitationStatus, int maxSize, Map<UUID, ClanGroupModel> members) {
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

    public ClanInvitationStatus getClanInvitationStatus() {
        return clanInvitationStatus;
    }

    public void setClanInvitationStatus(ClanInvitationStatus clanInvitationStatus) {
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

    public void addMember(ClanPlayer clanPlayer) {
        clanPlayer.setClan(this);

        var members = this.getMembers();
        members.put(clanPlayer.getUniqueId(), clanPlayer.getClanGroup());

        this.setMembers(members);

        this.reload();
        clanPlayer.reload();

        // TODO: Add to cache
    }

    public void reload() {
        LateTimeClan.getInstance().getClanDatabase().updateClan(this);
    }

    public void kick(ClanPlayer clanPlayer) {

        var currentMembers = this.getMembers();

        currentMembers.remove(clanPlayer.getUniqueId());
        this.setMembers(currentMembers);

        // TODO: Add cache
        LateTimeClan.getInstance().getClanDatabase().updateClan(this);
        clanPlayer.reload();

    }

    public void sendNotification(String message, String... highlights) {

        this.getMembers().forEach((uuid, clanGroupModel) -> {

            var bukkitOnlinePlayer = Bukkit.getPlayer(uuid);

            if (bukkitOnlinePlayer != null) {
                if (message.contains("%s") && highlights != null) {
                    bukkitOnlinePlayer.sendMessage(LateTimeClan.getInstance().getMessages().compileMessage(
                            message, highlights
                    ));
                    return;
                }
                bukkitOnlinePlayer.sendMessage(LateTimeClan.getInstance().getMessages().compileMessage(
                        message
                ));
            }

        });

    }

}


