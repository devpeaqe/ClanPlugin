package de.peaqe.latetimeclan.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
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

public class ClanSettingsPage {

    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanSettingsPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.clanModel = clanModel;
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

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanModel.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanModel.getName())
                .addLore(
                        "",
                        "§8• §7Hier kannst du Einstellungen für den Clan vornehmen."
                )
                .build();


        final var clanStatus = new ItemBuilder(Material.SPRUCE_DOOR)
                .setDisplayName("§8• §eClan Status")
                .addLore(
                        "",
                        "§8• §7Ändere den Clanstatus.",
                        "§8• §7Clanstatus: §e" + ClanUtil.getClanInvitationStatus(clanModel).getStatus()
                )
                .build();

        final var moderator = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§8• §eSoon...")
                .addLore(
                        "",
                        "§8• §7Looren kommen bald..."
                )
                .build();

        final var leader = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§8• §eSoon...")
                .addLore(
                        "",
                        "§8• §7Looren kommen bald..."
                )
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(29, clanStatus);
        this.inventory.setItem(31, moderator);
        this.inventory.setItem(33, leader);

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
