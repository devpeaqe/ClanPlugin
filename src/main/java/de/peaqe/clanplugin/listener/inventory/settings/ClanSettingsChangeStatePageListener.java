package de.peaqe.clanplugin.listener.inventory.settings;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.navigation.ClanInfoPage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsPage;
import de.peaqe.clanplugin.objects.ClanInvitationStatus;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.manager.UniqueIdManager;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanSettingsChangeStatePageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanSettingsChangeStatePageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Status ändern"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);
        if (clanPlayer == null) return;

        var clan = clanPlayer.getClan();
        if (clan == null) return;

        if (!clanPlayer.hasPermission(ClanAction.CHANGE_STATE)) {
            player.closeInventory();
            player.sendMessage(this.clanPlugin.getMessages().compileMessage(
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
                player.openInventory(new ClanInfoPage(this.clanPlugin, clan).getInventory(player));

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

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Status wechsel")
                                .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                .addField("Neuer Status", statusText, true)
                                .addField("Vorherige Gruppe", statusText1, true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(color)
                );

            }

            case 44 -> {
                player.closeInventory();
                player.openInventory(new ClanSettingsPage(this.clanPlugin, clanPlayer.getClan()).getInventory());
            }

        }

    }

    private ClanInvitationStatus getClanInvitationStatusFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (itemStack.getItemMeta() == null) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;
        if (itemStack.getItemMeta().displayName() == null) return null;

        var itemName = PlainTextComponentSerializer.plainText()
                .serialize(Objects.requireNonNull(itemStack.getItemMeta().displayName()));

        var clanInvitationStatusName = itemName.split("§8• ")[1];
        if (clanInvitationStatusName == null) return null;

        var targetUUID = UniqueIdManager.getUUID(clanInvitationStatusName);
        if (targetUUID == null) return null;

        return ClanInvitationStatus.getFromStatus(clanInvitationStatusName);
    }
}
