package de.peaqe.clanplugin.inventory.settings;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanInvitationStatus;
import de.peaqe.clanplugin.objects.ClanObject;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.util.ItemBuilder;
import de.peaqe.clanplugin.util.heads.Base64Compiler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Map;
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

    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanSettingsChangeStatePage(ClanPlugin clanPlugin, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(clanPlugin.getMessages().compileMessage(
                        "§8Status ändern"
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

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanObject.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanObject.getName())
                .setLore(
                        "",
                        "§8• §7Hier kannst du dein Clan Status einstellen.",
                        "§8• §7Aktueller Status: " + ClanUtil.getClanInvitationStatus(clanObject).getStatus(),
                        "",
                        "§8» §aÖffentlich§8 » §7Jeder kann den Clan beitreten.",
                        "§8» §eAuf Einladung §8» §7Man kann den Clan nur mit Einladung beitreten.",
                        "§8» §cGeschlossen §8» §7Keiner kann den Clan beitreten."
                )
                .build();


        var statusMaterials = Map.of(
                ClanInvitationStatus.OPEN, Material.LIME_STAINED_GLASS_PANE,
                ClanInvitationStatus.INVITATION, Material.ORANGE_STAINED_GLASS_PANE,
                ClanInvitationStatus.CLOSED, Material.RED_STAINED_GLASS_PANE
        );

        for (int i = 0; i < statusMaterials.size(); i++) {
            var status = ClanInvitationStatus.values()[i];
            var material = statusMaterials.get(status);

            final var clanStatusItem = new ItemBuilder(material)
                    .setDisplayName("§8• " + status.getStatus())
                    .addLore("", "§8• §7Setze den Clanstatus auf " + status.getStatus())
                    .glow(clanObject.getClanInvitationStatus().equals(status))
                    .build();

            this.inventory.setItem(29 + (i * 2), clanStatusItem);
        }

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
