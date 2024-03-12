package de.peaqe.latetimeclan.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
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

public class ClanMemberChangeGroupConfirmPage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanMemberChangeGroupConfirmPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Gruppenwechsel bestätigen"
                ))
        );
    }

    public void initializeInventory(ClanPlayer sender, ClanPlayer target, ClanGroupModel clanGroupModel) {

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
                .addLore(
                        " ",
                        "§8• §7Gruppe: " + target.getClanGroup().getColor() + target.getClanGroup().getName()
                )
                .build();


        final var decline = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§8• §cAblehnen")
                .addLore(
                        " ",
                        "§8• §7Kehre zurück zur Mitgliederliste zurück"
                )
                .glow()
                .build();

        final var confirm = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§8• §aAkzeptieren")
                .addLore(
                        " ",
                        "§8• §7Ändere die Clan-Gruppe von §e" + target.getName() + "§7 zum " +
                                clanGroupModel.getColor() + clanGroupModel.getName() + "§7."
                )
                .glow(ClanUtil.isPermitted(sender, target, ClanAction.KICK))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, decline);
        this.inventory.setItem(24, confirm);

    }

    public Inventory getInventory(ClanPlayer sender, ClanPlayer target, ClanGroupModel clanGroupModel) {
        this.initializeInventory(sender, target, clanGroupModel);
        return inventory;
    }

}
