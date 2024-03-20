package de.peaqe.latetimeclan.listener.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberPage;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
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

public class ClanMemberChangeGroupConfirmPageListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public ClanMemberChangeGroupConfirmPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Gruppenwechsel bestätigen"
                ))
        )) return;

        event.setCancelled(true);

        switch (event.getSlot()) {

            case 20 -> {
                // DECLINE
                player.closeInventory();

                var clanPlayer = ClanPlayer.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                this.lateTimeClan.getCache().remove(target.getUniqueId());
                player.openInventory(new ClanMemberPage(this.lateTimeClan, clanPlayer.getClan()).getInventory());
            }

            case 24 -> {

                // CONFIRM
                var clanPlayer = ClanPlayer.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanUtil.isPermitted(clanPlayer, target, ClanAction.CHANGE_GROUP)) {

                    if (!this.lateTimeClan.getCache().containsKey(target.getUniqueId())) return;
                    var clanGroupModel = this.lateTimeClan.getCache().get(target.getUniqueId());
                    var tmpClanGroupModel = target.getClanGroup();

                    if (target.getClanGroup().equals(clanGroupModel)) return;

                    target.setClanGroup(clanGroupModel);

                    player.closeInventory();

                    target.getClan().update();
                    player.openInventory(new ClanMemberPage(this.lateTimeClan, clanPlayer.getClan()).getInventory());

                    var targetOnlinePlayer = Bukkit.getPlayer(target.getUniqueId());
                    if (targetOnlinePlayer != null) {
                        targetOnlinePlayer.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                                "Du wurdest zum %s " +
                                        (tmpClanGroupModel.getPermissionLevel() > clanGroupModel.getPermissionLevel()
                                        ? "degradiert" : "befördert") + ".",
                                clanGroupModel.getColor() + clanGroupModel.getName()
                        ));
                    }

                    clanPlayer.getClan().sendNotification(
                            "Das Mitglied %s wurde von %s zum %s " +
                                    (tmpClanGroupModel.getPermissionLevel() > clanGroupModel.getPermissionLevel()
                                            ? "degradiert" : "befördert") + ".",
                            target.getName(),
                            player.getName(),
                            clanGroupModel.getColor() + clanGroupModel.getName()
                    );

                    this.lateTimeClan.getWebhookSender().sendWebhook(
                            new DiscordWebhook.EmbedObject().setTitle("Gruppenwechsel")
                                    .addField("Mitglied", target.getName(), true)
                                    .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                    .addField("Neue Gruppe", clanGroupModel.getName(), true)
                                    .addField("Vorherige Gruppe", tmpClanGroupModel.getName(), true)
                                    .addField("Clan", clanPlayer.getClan().getName(), true)
                                    .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                    .setFooter("× LateTimeMC.DE » Clan-System", null)
                                    .setColor(Color.ORANGE)
                    );

                    return;
                }

                // If it is not permitted
                player.closeInventory();
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "§cDu bist derzeit nicht berechtigt die Gruppe von %s §czu bearbeiten!",
                        target.getName()
                ));

            }

        }

    }

    private ClanPlayer getClanPlayerFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;

        var targetName = itemStack.getItemMeta().getDisplayName().split("§8• §e")[1];
        if (targetName == null) return null;

        var targetUUID = UniqueIdManager.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayer.fromPlayer(targetUUID);
    }

}
