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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.io.IOException;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanMemberKickConfirmPageListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public ClanMemberKickConfirmPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglied rausschmeißen"
                ))
        )) return;

        event.setCancelled(true);

        switch (event.getSlot()) {

            case 20 -> {
                // DECLINE
                player.closeInventory();
                player.openInventory(new ClanMemberPage(this.lateTimeClan, ClanPlayer.fromPlayer(player).getClan()).getInventory());
            }

            case 24 -> {

                // CONFIRM
                var clanPlayer = ClanPlayer.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanUtil.isPermitted(clanPlayer, target, ClanAction.KICK)) {

                    // Kick target from clan
                    clanPlayer.getClan().kick(target);

                    // Sender notify
                    player.closeInventory();
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                            "Der Spieler %s wurde erfolgreich aus deinem Clan geschmissen!",
                            target.getName()
                    ));
                    player.openInventory(new ClanMemberPage(this.lateTimeClan, clanPlayer.getClan()).getInventory());

                    // Target notify
                    var targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                                "Du wurdest aus dem Clan %s geschmissen!",
                                clanPlayer.getClan().getName()
                        ));
                    }

                    clanPlayer.getClan().sendNotification(
                            "Das Mitglied %s wurde von %s aus dem Clan geworfen.",
                            target.getName(),
                            player.getName()
                    );

                    try {
                        var webhoook = new DiscordWebhook();
                        var embed = new DiscordWebhook.EmbedObject();

                        //embed.setImage(ClanUtil.getPlayerHeadUrl(target.getName()));
                        embed.addField("Mitglied", target.getName(), true);
                        embed.addField("Sender", player.getName(), true);
                        embed.addField("Clan", clanPlayer.getClan().getName(), true);
                        embed.setTitle("Ein Mitglied wurde gekickt.");
                        embed.addField("Clan-Tag", clanPlayer.getClan().getTag(), true);
                        embed.setFooter("× LateTimeMC.DE » Clan-System", null);
                        embed.setColor(Color.RED);

                        webhoook.addEmbed(embed);
                        webhoook.execute();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    target.reload();
                    return;
                }

                // If it is not permitted
                player.closeInventory();
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "§cDu bist derzeit nicht berechtigt %s §caus dem Clan werfen zu können!",
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
