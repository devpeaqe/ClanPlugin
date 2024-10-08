package de.peaqe.clanplugin.inventory.settings;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanObject;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.util.ItemBuilder;
import de.peaqe.clanplugin.util.heads.Base64Compiler;
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
    private final ClanObject clanObject;

    public ClanSettingsModerateChatPage(ClanPlugin clanPlugin, ClanObject clanObject) {
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(clanPlugin.getMessages().compileMessage(
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

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(UUID.fromString(clanObject.getClanFounderUUID()));
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName("§8• §e" + clanObject.getName())
                .setLore(
                        "",
                        "§8• §7Hier kannst du einstellen, ob der Clan-Chat §cde§aaktiviert §7sein soll.",
                        "§8• §7Aktuell aktiviert: " +
                                (clanObject.getSettings().isClanChatToggled() ? "§aja" : "§cnein") + "§7.",
                        "",
                        "§8» §aAktiviert§8 » §7Jeder kann in den Clan-Chat schreiben.",
                        "§8» §cDeaktivert §8» §7Keiner kann in den Clan-Chat schreiben."
                )
                .build();

        var clanChatActivateItem = new ItemBuilder(Material.GREEN_DYE)
                .setDisplayName("§8• §aAktivieren")
                .setLore("", "§8• §aAktiviere §7den Clan-Chat sofern dieser deaktiviert ist.")
                .glow(clanObject.getSettings().isClanChatToggled())
                .build();

        var clanChatDeactivateItem = new ItemBuilder(Material.RED_DYE)
                .setDisplayName("§8• §cDeaktivieren")
                .setLore("", "§8• §cDeaktiviere §7den Clan-Chat sofern dieser aktiviert ist.")
                .glow(!clanObject.getSettings().isClanChatToggled())
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanChatActivateItem);
        this.inventory.setItem(24, clanChatDeactivateItem);
        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());

    }

    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

}
