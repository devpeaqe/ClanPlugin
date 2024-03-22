package de.peaqe.latetimeclan.inventory.deletion;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanObject;
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

public class ClanDeleteConfirmPage {

    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanDeleteConfirmPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Löschung"
                ))
        );
    }

    public void initializeInventory() {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

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
                .build();


        final var decline = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§8• §cAblehnen")
                .addLore(
                        " ",
                        "§8• §7Breche den Löschvorgang ab"
                )
                .glow()
                .build();

        final var confirm = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("§8• §aAkzeptieren")
                .addLore(
                        " ",
                        "§8• §7Lösche den Clan",
                        "§8• §c§lVORSICHT! §r§cDieser Vorgang kann §nnicht§r §crückgängig gemacht werden!",
                        "",
                        "§8• §7Was muss ich berücksichtigen?",
                        "§8• §cAlle Daten werden gelöscht",
                        "§8• §cDie Mitglieder werden informiert",
                        "§8• §cMan bekommt weder eine Erstattung noch eine Entschädigung"
                )
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, decline);
        this.inventory.setItem(24, confirm);

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
