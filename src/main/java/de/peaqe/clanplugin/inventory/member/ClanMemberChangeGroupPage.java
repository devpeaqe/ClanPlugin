package de.peaqe.clanplugin.inventory.member;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanGroup;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.util.ItemBuilder;
import de.peaqe.clanplugin.util.heads.Base64Compiler;
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

    private final Inventory inventory;

    public ClanMemberChangeGroupPage(ClanPlugin clanPlugin) {
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(clanPlugin.getMessages().compileMessage(
                        "§8Gruppe bearbeiten"
                ))
        );
    }

    public void initializeInventory(ClanPlayerObject target) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27,
                35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

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
                .setDisplayName("§8• §e" + target.getName())
                .setLore(
                        " ",
                        "§8• §7Aktuelle Gruppe: " + target.getClanGroup().getColor() + target.getClanGroup().getName()
                )
                .build();


        final var member = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§8• §7Mitglied")
                .setLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §7Mitglied"
                )
                .glow(target.getClanGroup().equals(ClanGroup.MEMBER))
                .build();

        final var moderator = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§8• §3Moderator")
                .setLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §3Moderator"
                )
                .glow(target.getClanGroup().equals(ClanGroup.MODERATOR))
                .build();

        final var leader = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§8• §cLeitung")
                .setLore(
                        " ",
                        "§8• §7Ändere den Rang des Mitglieds zum §cManager"
                )
                .glow(target.getClanGroup().equals(ClanGroup.MANAGER))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, member);
        this.inventory.setItem(31, moderator);
        this.inventory.setItem(33, leader);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory(ClanPlayerObject target) {
        this.initializeInventory(target);
        return inventory;
    }

}
