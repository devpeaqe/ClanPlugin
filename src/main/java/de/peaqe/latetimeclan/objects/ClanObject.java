package de.peaqe.latetimeclan.objects;

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

public class ClanObject {

    private String name, tag, clanFounderUuid;
    private ClanInvitationStatus clanInvitationStatus;
    private int maxSize;
    private Map<UUID, ClanGroup> members;
    private SettingsObject settingsObject;
    private int clanBankAmount;

    public ClanObject(String name, String tag, String clanFounderUuid, ClanInvitationStatus clanInvitationStatus,
                      int maxSize, Map<UUID, ClanGroup> members, SettingsObject settingsObject, int clanBankAmount) {
        this.name = name;
        this.tag = tag;
        this.clanFounderUuid = clanFounderUuid;
        this.clanInvitationStatus = clanInvitationStatus;
        this.maxSize = maxSize;
        this.members = members;
        this.settingsObject = settingsObject;
        this.clanBankAmount = clanBankAmount;
    }

    public ClanObject() {}

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

    public SettingsObject getSettings() {
        return settingsObject;
    }

    public void setSettings(SettingsObject settingsObject) {
        this.settingsObject = settingsObject;
    }

    public String getClanFounderUuid() {
        return clanFounderUuid;
    }

    public int getClanBankAmount() {
        return clanBankAmount;
    }

    public void setClanBankAmount(int clanBankAmount) {
        this.clanBankAmount = clanBankAmount;
    }

    public Map<UUID, ClanGroup> getMembers() {
        return members;
    }

    public void setMembers(Map<UUID, ClanGroup> members) {
        this.members = members;
    }
    public void delete() {
        LateTimeClan.getInstance().getClanSettingsDatabase().deleteClan(this);
        LateTimeClan.getInstance().getClanDatabase().deleteClan(this);
    }

    public void addMember(ClanPlayerObject clanPlayerObject) {
        clanPlayerObject.setClan(this);

        var members = this.getMembers();
        members.put(clanPlayerObject.getUniqueId(), clanPlayerObject.getClanGroup());

        this.setMembers(members);

        this.update();
        clanPlayerObject.reload();
    }

    public void update() {
        LateTimeClan.getInstance().getClanDatabase().updateClan(this);
    }

    public void kick(ClanPlayerObject clanPlayerObject) {

        var currentMembers = this.getMembers();

        currentMembers.remove(clanPlayerObject.getUniqueId());
        this.setMembers(currentMembers);

        LateTimeClan.getInstance().getClanDatabase().updateClan(this);
        clanPlayerObject.reload();

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


