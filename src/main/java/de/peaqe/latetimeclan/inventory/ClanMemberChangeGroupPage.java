package de.peaqe.latetimeclan.inventory;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 22.02.2024 | 21:09 Uhr
 * *
 */

public class ClanMemberChangeGroupPage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanMemberChangeGroupPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Clan-Gruppe bearbeiten"
                ))
        );
    }

    public void initializeInventory(ClanPlayer sender, ClanPlayer target) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(target.getUniqueId());
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName(" §8• §e" + target.getName())
                .addLore(
                        " ",
                        "§8• §6Aktuelle Gruppe: " + target.getClanGroup().getColor() + target.getClanGroup().getName()
                )
                .build();


        final var member = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName(" §8• §7Mitglied")
                .addLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §7Mitglied"
                )
                .glow(target.getClanGroup().equals(ClanGroupModel.MEMBER))
                .build();

        final var moderator = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName(" §8• §3Moderator")
                .addLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §3Moderator"
                )
                .glow(target.getClanGroup().equals(ClanGroupModel.MODERATOR))
                .build();

        final var leader = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName(" §8• §cLeitung")
                .addLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §cManager"
                )
                .glow(target.getClanGroup().equals(ClanGroupModel.MANAGER))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, member);
        this.inventory.setItem(31, moderator);
        this.inventory.setItem(33, leader);

    }

    public Inventory getInventory(ClanPlayer sender, ClanPlayer target) {
        this.initializeInventory(sender, target);
        return inventory;
    }

}
