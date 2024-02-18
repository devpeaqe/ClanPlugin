package de.peaqe.latetimeclan.models;

import de.peaqe.latetimeclan.models.util.ClanAction;
import org.bukkit.entity.Player;

import java.util.UUID;

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
    }

    public static ClanPlayer fromPlayer(Player player) {
        return new ClanPlayer(
                player.getName(),
                player.getUniqueId(),
                new ClanModel(), // TODO: Remove test data
                ClanGroupModel.OWNER);
    }

    public boolean hasPermission(ClanAction clanAction) {
        return clanGroup.getPermissionLevel() >= clanAction.getPermissionLevel();
    }

}
