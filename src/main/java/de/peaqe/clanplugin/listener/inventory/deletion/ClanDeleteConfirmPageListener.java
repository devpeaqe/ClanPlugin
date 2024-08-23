package de.peaqe.clanplugin.listener.inventory.deletion;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.awt.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanDeleteConfirmPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanDeleteConfirmPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Löschung"
                ))
        )) return;

        event.setCancelled(true);

        switch (event.getSlot()) {

            case 20 -> player.closeInventory();
            case 24 -> {

                // CONFIRM
                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                if (!clanPlayer.hasPermission(ClanAction.DELETE)) {
                    player.closeInventory();
                    return;
                }

                clanPlayer.getClan().sendNotification(
                        "Der Clan wurde von %s geöscht.",
                        "§c" + player.getName()
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan gelöscht")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Clan-Bank", ClanUtil
                                        .compressIntWithoutColor(clanPlayer.getClan().getClanBankAmount()), true)
                                .addField("Mitglieder", clanPlayer.getClan()
                                        .getMembers().size() + "", true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );

                this.clanPlugin.getClanInvitaionConfig().clearInvitations(clanPlayer.getClan());

                player.closeInventory();
                clanPlayer.getClan().delete();

            }
        }

    }

}
