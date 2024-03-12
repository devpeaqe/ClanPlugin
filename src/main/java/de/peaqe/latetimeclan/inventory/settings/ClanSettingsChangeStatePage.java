package de.peaqe.latetimeclan.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 22.02.2024 | 21:09 Uhr
 * *
 */

public class ClanSettingsChangeStatePage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanSettingsChangeStatePage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Clan-Status ändern"
                ))
        );
    }

    public void initializeInventory() {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42,
                43, 44};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanModel.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanModel.getName())
                .addLore(
                        "",
                        "§8• §7Hier kannst du dein Clan Status einstellen.",
                        "§8• §7Aktueller Status: " + ClanUtil.getClanInvitationStatus(clanModel).getStatus(),
                        "",
                        "§8» §aÖffentlich§8 » §7Jeder kann den Clan beitreten.",
                        "§8» §eAuf Einladung §8» §7Man kann den Clan nur mit Einladung beitreten.",
                        "§8» §cGeschlossen §8» §7Keiner kann den Clan beitreten."
                )
                .build();


        final var clanStatusOpen = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§8• " + ClanInvitationStatus.OPEN.getStatus())
                .addLore(
                        "",
                        "§8• Setze den Clanstatus auf " + ClanInvitationStatus.OPEN.getStatus()
                )
                .glow(clanModel.getClanInvitationStatus().equals(ClanInvitationStatus.OPEN))
                .build();

        final var clanStatusInvitation = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE)
                .setDisplayName("§8• " + ClanInvitationStatus.INVITATION.getStatus())
                .addLore(
                        "",
                        "§8• Setze den Clanstatus auf " + ClanInvitationStatus.OPEN.getStatus()
                )
                .glow(clanModel.getClanInvitationStatus().equals(ClanInvitationStatus.INVITATION))
                .build();

        final var clanStatusClosed = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§8• " + ClanInvitationStatus.CLOSED.getStatus())
                .addLore(
                        "",
                        "§8• Setze den Clanstatus auf " + ClanInvitationStatus.OPEN.getStatus()
                )
                .glow(clanModel.getClanInvitationStatus().equals(ClanInvitationStatus.CLOSED))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, clanStatusOpen);
        this.inventory.setItem(31, clanStatusInvitation);
        this.inventory.setItem(33, clanStatusClosed);

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
