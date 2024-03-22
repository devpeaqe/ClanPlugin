package de.peaqe.latetimeclan.listener.inventory.deletion;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.manager.UniqueIdManager;
import de.peaqe.latetimeclan.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

    private final LateTimeClan lateTimeClan;

    public ClanDeleteConfirmPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
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

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan gelöscht")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Clan-Bank", ClanUtil
                                        .compressIntWithoutColor(clanPlayer.getClan().getClanBankAmount()), true)
                                .addField("Mitglieder", clanPlayer.getClan()
                                        .getMembers().size() + "", true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );

                player.closeInventory();
                clanPlayer.getClan().delete();

            }
        }

    }

    private ClanPlayerObject getClanPlayerFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;

        var targetName = itemStack.getItemMeta().getDisplayName().split("§8• §e")[1];
        if (targetName == null) return null;

        var targetUUID = UniqueIdManager.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayerObject.fromPlayer(targetUUID);
    }


}
