package de.peaqe.latetimeclan.listener.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;

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

    public ClanSettingsChangeStatePageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Status ändern"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayer.fromPlayer(player);
        if (clanPlayer == null) return;

        var clan = clanPlayer.getClan();
        if (clan == null) return;

        if (!clanPlayer.hasPermission(ClanAction.CHANGE_STATE)) {
            player.closeInventory();
            player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                    "Du bist derzeit nicht berechtigt um den %s zu ändern!",
                    "Clan-Status"
            ));
            return;
        }

        switch (event.getSlot()) {
            case 29, 31, 33 -> {

                var clanInvitationStatus = this.getClanInvitationStatusFromItemStack(event.getCurrentItem());
                if (clanInvitationStatus == null) return;

                var tmpClanInvitationStatus = clan.getClanInvitationStatus();

                if (!clan.getClanInvitationStatus().equals(clanInvitationStatus)) {
                    clan.setClanInvitationStatus(clanInvitationStatus);
                    clan.update();
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
                }

                player.closeInventory();
                player.openInventory(new ClanInfoPage(this.lateTimeClan, clan).getInventory(player));

                clan.sendNotification(
                        "Der Clan-Status wurde von %s auf %s geändert.",
                        tmpClanInvitationStatus.getStatus(),
                        clan.getClanInvitationStatus().getStatus()
                );

                var color = (clanInvitationStatus.equals(ClanInvitationStatus.CLOSED) ? Color.RED : (
                        clanInvitationStatus.equals(ClanInvitationStatus.OPEN) ? Color.GREEN : Color.ORANGE));

                var statusText = (clanInvitationStatus.equals(ClanInvitationStatus.CLOSED) ? "Geschlossen" : (
                        clanInvitationStatus.equals(ClanInvitationStatus.OPEN) ? "Öffentlich" : "Auf Einladung"));

                var statusText1 = (tmpClanInvitationStatus.equals(ClanInvitationStatus.CLOSED) ? "Geschlossen" : (
                        tmpClanInvitationStatus.equals(ClanInvitationStatus.OPEN) ? "Öffentlich" : "Auf Einladung"));

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Status wechsel")
                                .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                .addField("Neuer Status", statusText, true)
                                .addField("Vorherige Gruppe", statusText1, true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor(color)
                );

            }
        }

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
