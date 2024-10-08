package de.peaqe.clanplugin.objects;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.manager.UniqueIdManager;
import lombok.AllArgsConstructor;
import lombok.Data;
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

@Data
@AllArgsConstructor
public class ClanPlayerObject {

    private String name;
    private UUID uniqueId;
    private ClanObject clan;
    private ClanGroup clanGroup;

    public void setClanGroup(ClanGroup clanGroup) {

        this.clanGroup = clanGroup;

        var clan = this.getClan();
        var members = clan.getMembers();

        if (members.containsKey(this.getUniqueId())) {
            members.remove(this.getUniqueId());
            members.put(this.getUniqueId(), clanGroup);
        }

        clan.setMembers(members);
        clan.update();
    }

    public void sendMessage(String message) {

        this.getClan().getMembers().forEach((uuid, clanGroupModel) -> {

            var bukkitOnlinePlayer = Bukkit.getPlayer(uuid);

            if (bukkitOnlinePlayer != null) {
                bukkitOnlinePlayer.sendMessage(message);
            }

        });
    }

    public void reload() {
        if (this.clan.getMembers().containsKey(this.uniqueId)) return;
        this.setNull();
    }

    public static ClanPlayerObject fromPlayer(Player player) {

        var clan = ClanPlugin.getInstance().getClanDatabase().getClanModelOfMember(player.getUniqueId());
        var clanGroupModel = getClanGroupModel(player.getUniqueId());

        if (clan == null || clanGroupModel == null) return null;

        return new ClanPlayerObject(
                player.getName(),
                player.getUniqueId(),
                ClanPlugin.getInstance().getClanDatabase().getClanModelOfMember(player.getUniqueId()),
                getClanGroupModel(player.getUniqueId())
        );
    }

    public static ClanPlayerObject fromPlayer(UUID uniqueId) {
        return new ClanPlayerObject(
                UniqueIdManager.getName(uniqueId),
                uniqueId,
                ClanPlugin.getInstance().getClanDatabase().getClanModelOfMember(uniqueId),
                getClanGroupModel(uniqueId)
        );
    }

    public boolean hasPermission(ClanAction clanAction) {
        return clanGroup.getPermissionLevel() >= clanAction.getPermissionLevel();
    }

    public static ClanGroup getClanGroupModel(UUID uniqueId) {

        var clan = ClanPlugin.getInstance().getClanDatabase().getClanModelOfMember(uniqueId);
        if (clan == null) return null;
        if (!clan.getMembers().containsKey(uniqueId)) return null;

        var atomicClanGroupModel = new AtomicReference<ClanGroup>();

        clan.getMembers().forEach((uuid, clanGroupModel) -> {
            if (uuid.equals(uniqueId)) {
                atomicClanGroupModel.set(clanGroupModel);
            }
        });

        return atomicClanGroupModel.get();

    }

    public void setNull() {
        if (this.getName() != null) this.setName(null);
        if (this.getUniqueId() != null) this.setUniqueId(null);
        if (this.getClanGroup() != null) this.setClanGroup(null);
        if (this.getClan() != null) this.setClan(null);
    }
}
