package de.peaqe.latetimeclan.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanObject;
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

public class ClanSettingsToggleBankPage {

    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanSettingsToggleBankPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Bank verwalten"
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
                .addLore(
                        "",
                        "§8• §7Hier kannst du einstellen, ob die Clan-Bank für jenden sichtbar sein soll.",
                        "§8• §7Aktuell aktiviert: " +
                                (clanObject.getSettings().isClanBankToggled() ? "§aja" : "§cnein") + "§7.",
                        "",
                        "§8» §aAktiviert§8 » §7Jeder kann den Kontostand einsehen.",
                        "§8» §cDeaktivert §8» §7Nur Clan-Mitglieder können den Kontostand einsehen."
                )
                .build();

        var clanBankActivateItem = new ItemBuilder(Material.GREEN_DYE)
                .setDisplayName("§8• §aAktivieren")
                .addLore("", "§8• §aAktiviere §7die Sichtbarkeit der Clan-Bank.")
                .glow(clanObject.getSettings().isClanBankToggled())
                .build();

        var clanBankDeactivateItem = new ItemBuilder(Material.RED_DYE)
                .setDisplayName("§8• §cDeaktivieren")
                .addLore("", "§8• §cDeaktiviere §7die Sichtbarkeit der Clan-Bank.")
                .glow(!clanObject.getSettings().isClanBankToggled())
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanBankActivateItem);
        this.inventory.setItem(24, clanBankDeactivateItem);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
