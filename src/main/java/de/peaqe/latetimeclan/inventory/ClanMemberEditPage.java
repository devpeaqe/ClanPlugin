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
import org.bukkit.inventory.Inventory;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 22.02.2024 | 21:09 Uhr
 * *
 */

public class ClanMemberEditPage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanModel clanModel;

    public ClanMemberEditPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*4,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder verwalten"
                ))
        );
    }

    public void initializeInventory(ClanPlayer sender, ClanPlayer target) {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setDisplayName("")
                            .build()
            );
        }

        var clanTargetSkull = Base64Compiler.getPlayerHeadFromUUID(target.getUniqueId());
        final var clanNameItem = new ItemBuilder(clanTargetSkull)
                .setDisplayName(" §8• §e" + target.getName())
                .addLore(
                        " ",
                        "§8• §7Gruppe: " + target.getClanGroup().getColor() + target.getClanGroup().getName()
                )
                .build();


        final var clanKickItem = new ItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName(" §8• §cRausschmeißen")
                .addLore(
                        " ",
                        "§8• §7Schmeiße §e" + target.getName() + "§7 aus dem Clan.",
                        " §8» §cVORSICHT! Dieser Vorgang kann §nnicht§r §crückgängig gemacht werden!",
                        " ",
                        "§8• §7Berechtigt: " + (isPermitted(sender, target, ClanAction.KICK) ? "§aJa" : "§cNein")
                )
                .glow(isPermitted(sender, target, ClanAction.KICK))
                .build();

        final var clanChangeGroupItem = new ItemBuilder(Material.GRASS_BLOCK)
                .setDisplayName("§8• §aRang verwaltung")
                .addLore(
                        " ",
                        "§8• §7Verwalte die Rollen von §e" + target.getName() + "§7.",
                        "§8• §7Berechtigt: " + (isPermitted(sender, target, ClanAction.CHANGE_GROUP) ? "§aJa" : "§cNein")
                )
                .glow(isPermitted(sender, target, ClanAction.CHANGE_GROUP))
                .build();

        this.inventory.setItem(13, clanNameItem);
        this.inventory.setItem(20, clanKickItem);
        this.inventory.setItem(24, clanChangeGroupItem);

    }

    public Inventory getInventory(ClanPlayer sender, ClanPlayer target) {
        this.initializeInventory(sender, target);
        return inventory;
    }

    public static boolean isPermitted(ClanPlayer sender, ClanPlayer target, ClanAction clanAction) {
        return (sender.hasPermission(clanAction) &&
                sender.getClanGroup().getPermissionLevel() > target.getClanGroup().getPermissionLevel());

    }

}
