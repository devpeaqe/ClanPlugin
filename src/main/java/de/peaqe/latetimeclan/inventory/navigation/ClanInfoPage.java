package de.peaqe.latetimeclan.inventory.navigation;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.color.Hex;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import de.peaqe.latetimeclan.util.heads.Head;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Date;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 22.02.2024 | 21:09 Uhr
 * *
 */

public class ClanInfoPage {

    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanInfoPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Informationen"
                ))
        );
    }

    public void initializeInventory(Player player) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanOwnerUUID = UUID.fromString(clanObject.getClanFounderUUID());
        var clanOwnerName = ClanPlayerObject.fromPlayer(clanOwnerUUID).getName();

        var clanOwnerSkull = Base64Compiler.getPlayerHeadFromUUID(clanOwnerUUID);
        final var clanNameItem = new ItemBuilder(clanOwnerSkull)
                .setDisplayName("§8• §e" + clanObject.getName())
                .setLore(
                        " ",
                        "§8• §7Clan-Tag: §a" + Hex.color(clanObject.getColor()) + clanObject.getTag(),
                        "§8• §7Status: §a" + ClanUtil.getClanInvitationStatus(clanObject).getStatus(),
                        "§8• §7Besitzer: §4" + clanOwnerName
                )
                .glow()
                .build();


        final var clanMemberItem = new ItemBuilder(Head.compile(Head.NERD))
                .setDisplayName("§8• §eMitglieder")
                .addLore(
                        " ",
                        "§8• §7Zeige dir die aktuellen Mitglieder des Clans an.",
                        "§8• §7Mitglieder: §a" + clanObject.getMembers().size() + "§8/§c" + clanObject.getMaxSize()
                )
                .build();

        final var clanSettingsItem = new ItemBuilder(Head.compile(Head.SETTINGS))
                .setDisplayName("§8• §eEinstellungen")
                .addLore(
                        " ",
                        "§8• §7Zeige dir die Einstellungen des Clans an und verwalte diese."
                )
                .build();

        final var clanStaticsItem = new ItemBuilder(Head.compile(Head.KING))
                .setDisplayName("§8• §aStatistiken")
                .addLore(
                        "",
                        "§8• §7Bank: " + ClanUtil.compressInt(clanObject.getClanBankAmount()) + "§7€",
                        "§8• §7Erstellt am: §c" +
                                ClanUtil.formatBerlinTimeDate(new Date(clanObject.getDateCreated().getTime()), "§c")
                )
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, clanStaticsItem);
        this.inventory.setItem(33, clanMemberItem);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);
        if (clanPlayer == null) return;

        if (clanPlayer.hasPermission(ClanAction.DELETE))
            this.inventory.setItem(31, clanSettingsItem);
    }

    public Inventory getInventory(Player player) {
        this.initializeInventory(player);
        return inventory;
    }

}
