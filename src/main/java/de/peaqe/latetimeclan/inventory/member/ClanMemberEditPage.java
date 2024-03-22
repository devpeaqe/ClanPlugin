package de.peaqe.latetimeclan.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
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

public class ClanMemberEditPage {

    private final Inventory inventory;

    public ClanMemberEditPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder verwalten"
                ))
        );
    }

    public void initializeInventory(ClanPlayerObject sender, ClanPlayerObject target) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

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
                        "§8• §7Gruppe: " + target.getClanGroup().getColor() + target.getClanGroup().getName()
                )
                .build();


        final var clanKickItem = new ItemBuilder(Material.TNT)
                .setDisplayName("§8• §cRausschmeißen")
                .setLore(
                        " ",
                        "§8• §7Schmeiße §e" + target.getName() + "§7 aus dem Clan.",
                        " §8» §cDieser Vorgang kann §nnicht§r §crückgängig gemacht werden!",
                        "§8• §7Berechtigt: " +
                                (ClanUtil.isPermitted(sender, target, ClanAction.KICK) ? "§aJa" : "§cNein")
                )
                .glow(ClanUtil.isPermitted(sender, target, ClanAction.KICK))
                .build();

        final var clanChangeGroupItem = new ItemBuilder(Material.ENCHANTED_BOOK)
                .setDisplayName("§8• §aRang Verwaltung")
                .setLore(
                        " ",
                        "§8• §7Verwalte die Rollen von §e" + target.getName() + "§7.",
                        "§8• §7Berechtigt: " +
                                (ClanUtil.isPermitted(sender, target, ClanAction.CHANGE_GROUP) ? "§aJa" : "§cNein")
                )
                .glow(ClanUtil.isPermitted(sender, target, ClanAction.CHANGE_GROUP))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanKickItem);
        this.inventory.setItem(24, clanChangeGroupItem);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory(ClanPlayerObject sender, ClanPlayerObject target) {
        this.initializeInventory(sender, target);
        return inventory;
    }

}
