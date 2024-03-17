package de.peaqe.latetimeclan.models;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.uuid.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:34 Uhr
 * *
 */

public class ClanPlayer {

    private String name;
    private UUID uniqueId;
    private ClanModel clan;
    private ClanGroupModel clanGroup;

    public ClanPlayer(String name, UUID uniqueId, ClanModel clan, ClanGroupModel clanGroup) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.clan = clan;
        this.clanGroup = clanGroup;
    }

    public ClanPlayer() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public ClanModel getClan() {
        return clan;
    }

    public void setClan(ClanModel clan) {
        this.clan = clan;
    }

    public ClanGroupModel getClanGroup() {
        return clanGroup;
    }

    public void setClanGroup(ClanGroupModel clanGroup) {

        this.clanGroup = clanGroup;

        var clan = this.getClan();
        var members = clan.getMembers();

        if (members.containsKey(this.getUniqueId())) {
            members.remove(this.getUniqueId());
            members.put(this.getUniqueId(), clanGroup);
        }

        clan.setMembers(members);
        clan.reload();
    }

    public void sendMessage(String message) {

        this.getClan().getMembers().forEach((uuid, clanGroupModel) -> {

            var bukkitOnlinePlayer = Bukkit.getPlayer(uuid);

            if (bukkitOnlinePlayer != null) {
                bukkitOnlinePlayer.sendMessage(LateTimeClan.getInstance().getMessages().sendClanMessage(
                        this, message
                ));
            }

        });
    }

    public void reload() {
        if (!this.clan.getMembers().containsKey(this.uniqueId)) {
            this.setNull();
        }
    }

    public static ClanPlayer fromPlayer(Player player) {

        var clan = LateTimeClan.getInstance().getClanDatabase().getClanModelOfMember(player.getUniqueId());
        var clanGroupModel = getClanGroupModel(player.getUniqueId());

        if (clan == null || clanGroupModel == null) return null;

        return new ClanPlayer(
                player.getName(),
                player.getUniqueId(),
                LateTimeClan.getInstance().getClanDatabase().getClanModelOfMember(player.getUniqueId()),
                getClanGroupModel(player.getUniqueId())
        );
    }

    public static ClanPlayer fromPlayer(UUID uniqueId) {
        return new ClanPlayer(
                UUIDFetcher.getName(uniqueId),
                uniqueId,
                LateTimeClan.getInstance().getClanDatabase().getClanModelOfMember(uniqueId),
                getClanGroupModel(uniqueId)
        );
    }

    public boolean hasPermission(ClanAction clanAction) {
        return clanGroup.getPermissionLevel() >= clanAction.getPermissionLevel();
    }

    public static ClanGroupModel getClanGroupModel(UUID uniqueId) {

        var clan = LateTimeClan.getInstance().getClanDatabase().getClanModelOfMember(uniqueId);
        if (clan == null) return null;
        if (!clan.getMembers().containsKey(uniqueId)) return null;

        var atomicClanGroupModel = new AtomicReference<ClanGroupModel>();

        clan.getMembers().forEach((uuid, clanGroupModel) -> {
            if (uuid.equals(uniqueId)) {
                atomicClanGroupModel.set(clanGroupModel);
            }
        });

        return atomicClanGroupModel.get();

    }

    public void setNull() {
        this.setClan(null);
        this.setClanGroup(null);
        this.setName(null);
        this.setUniqueId(null);
    }
}
