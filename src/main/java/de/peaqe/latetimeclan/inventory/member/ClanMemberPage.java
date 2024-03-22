package de.peaqe.latetimeclan.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.provider.util.HeadProperty;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 21.02.2024 | 19:42 Uhr
 * *
 */

public class ClanMemberPage {

    private final LateTimeClan lateTimeClan;
    private final Inventory inventory;
    private final ClanObject clanObject;

    public ClanMemberPage(LateTimeClan lateTimeClan, ClanObject clanObject) {
        this.lateTimeClan = lateTimeClan;
        this.clanObject = clanObject;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder",
                        this.clanObject.getName()
                ))
        );
    }

    private void initializeInventory() {

        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};

        for (var borderItemSlot : borderItemSlots) {
            this.inventory.setItem(
                    borderItemSlot,
                    new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .setDisplayName("")
                        .build()
            );
        }

        var sortedMembers = new TreeMap<>(Comparator.comparingInt((UUID uuid) -> clanObject.getMembers().get(uuid).getPermissionLevel())
                .thenComparing(UUID::toString).reversed());

        sortedMembers.putAll(clanObject.getMembers());

        sortedMembers.forEach((uuid, clanGroupModel) -> {

            var tempClanGroupModel = (ClanGroup) clanGroupModel;

            var clanPlayer = ClanPlayerObject.fromPlayer(uuid);

            var clanPlayerHead = this.lateTimeClan.getHeadDatabase()
                    .getHead(HeadProperty.UUID, uuid.toString()).orElse(null);
            if (clanPlayerHead == null) clanPlayerHead = this.lateTimeClan.getHeadManager().registerHead(uuid);

            var playerHead = new ItemBuilder(clanPlayerHead)
                    .setDisplayName("§8• §a" + clanPlayer.getName())
                    .addLore(
                            " ",
                            "§8• §7Gruppe: " + tempClanGroupModel.getColor() + tempClanGroupModel.getName(),
                            "§8• §7Status: " + (Bukkit.getPlayer(uuid) != null ? "§aOnline" : "§cOffline")
                    )
                    .addLore(
                            Bukkit.getPlayer(uuid) == null,
                            "§8• §7Zuletzt online: §cXX:XX"
                    )
                    .build();

            var nextAvailableSlot = this.getNextAvailableSlot();
            if (nextAvailableSlot != -1) this.inventory.setItem(nextAvailableSlot, playerHead);
        });

        this.inventory.setItem(borderItemSlots[borderItemSlots.length - 1], ClanUtil.getGoBackItem());
    }


    public Inventory getInventory() {
        this.initializeInventory();
        return inventory;
    }

    private int getNextAvailableSlot() {
        var borderItemSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42};
        for (int i = 0; i < 9 * 5; i++) {
            if (!ArrayUtils.contains(borderItemSlots, i) && this.inventory.getItem(i) == null) {
                return i;
            }
        }
        return -1;
    }

}
