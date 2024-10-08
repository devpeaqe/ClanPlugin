package de.peaqe.clanplugin.inventory.member;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
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

public class ClanMemberKickConfirmPage {

    private final Inventory inventory;

    public ClanMemberKickConfirmPage(ClanPlugin clanPlugin) {
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(clanPlugin.getMessages().compileMessage(
                        "§8Mitglied rausschmeißen"
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


        final var decline = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§8• §cAblehnen")
                .setLore(
                        " ",
                        "§8• §7Kehre zurück zur Mitgliederliste"
                )
                .glow()
                .build();

        final var confirm = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§8• §aAkzeptieren")
                .setLore(
                        " ",
                        "§8• §7Schmeiße §e" + target.getName() + "§7 aus dem Clan.",
                        "§8• §c§lVORSICHT! §r§cDieser Vorgang kann §nnicht§r §crückgängig gemacht werden!"
                )
                .glow(ClanUtil.isPermitted(sender, target, ClanAction.KICK))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, decline);
        this.inventory.setItem(24, confirm);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory(ClanPlayerObject sender, ClanPlayerObject target) {
        this.initializeInventory(sender, target);
        return inventory;
    }

}
