package de.peaqe.latetimeclan.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import de.peaqe.latetimeclan.util.heads.Head;
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

public class ClanSettingsPage {

    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanSettingsPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Einstellungen"
                ))
        );
    }

    public void initializeInventory() {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanObject.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanObject.getName())
                .setLore(
                        "",
                        "§8• §7Hier kannst du Einstellungen für den Clan vornehmen."
                )
                .build();


        final var clanStatusItem = new ItemBuilder(Head.compile(Head.BATTERY))
                .setDisplayName("§8• §eClan Status")
                .setLore(
                        "",
                        "§8• §7Hier kannst du den Clanstatus einstellen.",
                        "§8• §7Clanstatus: §e" + ClanUtil.getClanInvitationStatus(clanObject).getStatus()
                )
                .build();

        final var clanChatItem = new ItemBuilder(Head.compile(Head.PAPER_PEN))
                .setDisplayName("§8• §eClan Chat")
                .setLore(
                        "",
                        "§8• §7Hier kannst du den Clan-Chat §aein§8-/§causschalten§7.",
                        "§8• §7Clan-Chat aktiv: §e" +
                                (clanObject.getSettings().isClanChatToggled() ? "§aja" : "§cnein") + "§7."
                )
                .build();

        final var clanBankItem = new ItemBuilder(Head.compile(Head.PIGGY_BANK))
                .setDisplayName("§8• §eClan Bank")
                .setLore(
                        "",
                        "§8• §7Hier kannst du die Sichtbarkeit der Clan-Bank einstellen.",
                        "§8• §7Clan-Bank sichtbar für: §e" +
                                (clanObject.getSettings().isClanBankToggled() ? "§ajeden" : "§cClan-Mitglieder") + "§7."
                )
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, clanStatusItem);
        this.inventory.setItem(31, clanChatItem);
        this.inventory.setItem(33, clanBankItem);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
