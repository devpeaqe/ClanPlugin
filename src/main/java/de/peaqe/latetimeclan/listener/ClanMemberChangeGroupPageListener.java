package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.ClanMemberChangeGroupConfirmPage;
import de.peaqe.latetimeclan.inventory.ClanMemberEditPage;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.UUIDFetcher;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanMemberChangeGroupPageListener implements Listener {

    private final LateTimeClan lateTimeClan;
    private final Map<UUID, ClanGroupModel> cache;

    public ClanMemberChangeGroupPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
        this.cache = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Clan-Gruppe bearbeiten"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayer.fromPlayer(player);

        if (clanPlayer == null) return;
        if (!clanPlayer.hasPermission(ClanAction.CHANGE_GROUP)) return;

        var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));

        if (target == null) return;
        if (!ClanMemberEditPage.isPermitted(clanPlayer, target, ClanAction.CHANGE_GROUP)) return;

        switch (event.getSlot()) {

            case 29 -> {
                if (target.getClanGroup().equals(ClanGroupModel.MEMBER)) return;

                //target.setClanGroup(ClanGroupModel.MEMBER);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroupModel.MEMBER);

                //clanPlayer.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan, clanPlayer.getClan())
                        .getInventory(clanPlayer, target, ClanGroupModel.MEMBER));

            }

            case 31 -> {
                if (target.getClanGroup().equals(ClanGroupModel.MODERATOR)) return;

                //target.setClanGroup(ClanGroupModel.MODERATOR);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroupModel.MODERATOR);

               //target.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan, clanPlayer.getClan())
                        .getInventory(clanPlayer, target, ClanGroupModel.MODERATOR));
            }

            case 33 -> {
                if (target.getClanGroup().equals(ClanGroupModel.MANAGER)) return;

                //target.setClanGroup(ClanGroupModel.MANAGER);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroupModel.MANAGER);

                //target.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan, clanPlayer.getClan())
                        .getInventory(clanPlayer, target, ClanGroupModel.MANAGER));

            }

        }

    }

    private ClanPlayer getClanPlayerFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;

        var targetName = itemStack.getItemMeta().getDisplayName().split("§8• §e")[1];
        if (targetName == null) return null;

        var targetUUID = UUIDFetcher.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayer.fromPlayer(targetUUID);
    }

    public Map<UUID, ClanGroupModel> getCache() {
        return cache;
    }
}
