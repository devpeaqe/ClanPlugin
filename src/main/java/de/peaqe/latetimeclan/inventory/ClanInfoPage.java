package de.peaqe.latetimeclan.inventory;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.heads.Base64Compiler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

public class ClanInfoPage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanInfoPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Informationen"
                ))
        );
    }

    public void initializeInventory(Player player) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanOwnerUUID = UUID.fromString(clanModel.getClanFounderUUID());
        var clanOwnerName = ClanPlayer.fromPlayer(clanOwnerUUID).getName();

        var clanOwnerSkull = Base64Compiler.getPlayerHeadFromUUID(clanOwnerUUID);
        final var clanNameItem = new ItemBuilder(clanOwnerSkull)
                .setDisplayName(" §8• §e" + clanModel.getName())
                .addLore(
                        " ",
                        "§8• §7Clan-Tag: §a" + clanModel.getTag(),
                        "§8• §7Mitglieder: §a" + clanModel.getMembers().size() + "§8/§c" + clanModel.getMaxSize(),
                        "§8• §7Besitzer: §4" + clanOwnerName
                )
                .glow()
                .build();


        final var clanMemberItem = new ItemBuilder(Material.OAK_SIGN)
                .setDisplayName(" §8• §eMitglieder")
                .addLore(
                        " ",
                        "§8• §7Zeige dir die aktuellen Mitglieder des Clans an.",
                        "§8• §7Mitglieder: §a" + clanModel.getMembers().size() + "§8/§c" + clanModel.getMaxSize()
                )
                .build();

        final var clanSettingsItem = new ItemBuilder(Material.PAPER)
                .setDisplayName("§8• §eEinstellungen")
                .addLore(
                        " ",
                        "§8• §7Zeige dir die Einstellungen des Clans an und verwalte diese."
                )
                .build();

        // TODO: Add founderDate to Database
        final var clanStaticsItem = new ItemBuilder(Material.PAPER).build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanStaticsItem);
        this.inventory.setItem(24, clanMemberItem);

        if (ClanPlayer.fromPlayer(player).hasPermission(ClanAction.DELETE))
            this.inventory.setItem(22, clanSettingsItem);


    }

    public Inventory getInventory(Player player) {
        this.initializeInventory(player);
        return inventory;
    }
}
