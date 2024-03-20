package de.peaqe.latetimeclan.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanModel;
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

public class ClanSettingsModerateChatPage {

    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanSettingsModerateChatPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(lateTimeClan.getMessages().compileMessage(
                        "§8Chat verwalten"
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

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanModel.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanModel.getName())
                .addLore(
                        "",
                        "§8• §7Hier kannst du einstellen, ob der Clan-Chat §cde§aaktiviert §7sein soll.",
                        "§8• §7Aktuell aktiviert: " +
                                (clanModel.getSettings().isClanChatToggled() ? "§aja" : "§cnein") + "§7.",
                        "",
                        "§8» §aAktiviert§8 » §7Jeder kann in den Clan-Chat schreiben.",
                        "§8» §cDeaktivert §8» §7Keiner kann in den Clan-Chat schreiben."
                )
                .build();

        var clanChatActivateItem = new ItemBuilder(Material.GREEN_DYE)
                .setDisplayName("§8• §aAktivieren")
                .addLore("", "§8• §aAktiviere §7den Clan-Chat sofern dieser deaktiviert ist.")
                .glow(clanModel.getSettings().isClanChatToggled())
                .build();

        var clanChatDeactivateItem = new ItemBuilder(Material.RED_DYE)
                .setDisplayName("§8• §cDeaktivieren")
                .addLore("", "§8• §cDeaktiviere §7den Clan-Chat sofern dieser aktiviert ist.")
                .glow(!clanModel.getSettings().isClanChatToggled())
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanChatActivateItem);
        this.inventory.setItem(24, clanChatDeactivateItem);

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
