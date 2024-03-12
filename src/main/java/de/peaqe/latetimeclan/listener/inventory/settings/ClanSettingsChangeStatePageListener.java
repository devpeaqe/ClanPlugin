package de.peaqe.latetimeclan.listener.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.uuid.UUIDFetcher;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
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

public class ClanSettingsChangeStatePageListener implements Listener {

    private final LateTimeClan lateTimeClan;
    private final Map<UUID, ClanGroupModel> cache;

    public ClanSettingsChangeStatePageListener(LateTimeClan lateTimeClan) {
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
                        "§8Clan-Status ändern"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayer.fromPlayer(player);
        if (clanPlayer == null) return;

        var clan = clanPlayer.getClan();
        if (clan == null) return;
        if (!clanPlayer.hasPermission(ClanAction.CHANGE_STATE)) return;

        switch (event.getSlot()) {
            case 29, 31, 33 -> {

                var clanInvitationStatus = this.getClanInvitationStatusFromItemStack(event.getCurrentItem());
                if (clanInvitationStatus == null) return;

                if (!clan.getClanInvitationStatus().equals(clanInvitationStatus)) {
                    clan.setClanInvitationStatus(clanInvitationStatus);
                    clan.reload();
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
                }

                player.closeInventory();
                player.openInventory(new ClanInfoPage(this.lateTimeClan, clan).getInventory(player));
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

    @Nullable
    private ClanInvitationStatus getClanInvitationStatusFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;

        var clanInvitationStatusName = itemStack.getItemMeta().getDisplayName().split("§8• ")[1];
        if (clanInvitationStatusName == null) return null;

        return ClanInvitationStatus.getFromStatus(clanInvitationStatusName);
    }

}
