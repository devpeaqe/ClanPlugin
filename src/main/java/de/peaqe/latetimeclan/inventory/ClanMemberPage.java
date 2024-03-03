package de.peaqe.latetimeclan.inventory;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.util.ItemBuilder;
import de.peaqe.latetimeclan.util.PlayerHeadFetcher;
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
    private Inventory inventory;
    private final ClanModel clanModel;

    public ClanMemberPage(LateTimeClan lateTimeClan, ClanModel clanModel) {
        this.lateTimeClan = lateTimeClan;
        this.clanModel = clanModel;
        this.inventory = Bukkit.createInventory(
                null,
                9*5,
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder",
                        this.clanModel.getName()
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

        var sortedMembers = new TreeMap<>(Comparator.comparingInt((UUID uuid) -> clanModel.getMembers().get(uuid).getPermissionLevel())
                .thenComparing(UUID::toString).reversed());

        sortedMembers.putAll(clanModel.getMembers());


        sortedMembers.forEach((uuid, clanGroupModel) -> {

            var tempClanGroupModel = (ClanGroupModel) clanGroupModel;

            var clanPlayer = ClanPlayer.fromPlayer(uuid);
            var playerHead = new ItemBuilder(PlayerHeadFetcher.getPlayerHeadFromUUID(uuid))
                    .setDisplayName("§8• §a" + clanPlayer.getName())
                    .addLore(
                            " ",
                            "§7Gruppe: " + tempClanGroupModel.getColor() + tempClanGroupModel.getName()
                    )
                    .build();

            var nextAvailableSlot = this.getNextAvailableSlot();
            if (nextAvailableSlot != -1) this.inventory.setItem(nextAvailableSlot, playerHead);

        });

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
