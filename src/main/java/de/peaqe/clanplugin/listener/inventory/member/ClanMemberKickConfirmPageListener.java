package de.peaqe.clanplugin.listener.inventory.member;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.member.ClanMemberEditPage;
import de.peaqe.clanplugin.inventory.member.ClanMemberPage;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.ClanUtil;
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

public class ClanMemberKickConfirmPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanMemberKickConfirmPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Mitglied rausschmeißen"
                ))
        )) return;

        event.setCancelled(true);

        switch (event.getSlot()) {

            case 20 -> {
                // DECLINE
                player.closeInventory();

                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                player.openInventory(new ClanMemberPage(this.clanPlugin, clanPlayer.getClan()).getInventory());
            }

            case 24 -> {

                // CONFIRM
                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanUtil.isPermitted(clanPlayer, target, ClanAction.KICK)) {

                    // Sender notify
                    player.closeInventory();
                    player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Der Spieler %s wurde erfolgreich aus deinem Clan geschmissen!",
                            target.getName()
                    ));
                    player.openInventory(new ClanMemberPage(this.clanPlugin, clanPlayer.getClan()).getInventory());

                    // Target notify
                    var targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(this.clanPlugin.getMessages().compileMessage(
                                "Du wurdest aus dem Clan %s geschmissen!",
                                clanPlayer.getClan().getName()
                        ));
                    }

                    clanPlayer.getClan().sendNotification(
                            "Das Mitglied %s wurde von %s aus dem Clan geworfen.",
                            target.getName(),
                            player.getName()
                    );

                    this.clanPlugin.getWebhookSender().sendWebhook(
                            new DiscordWebhook.EmbedObject().setTitle("Ein Mitglied wurde gekickt.")
                                    .addField("Mitglied", target.getName(), true)
                                    .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                    .addField("Clan", clanPlayer.getClan().getName(), true)
                                    .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                    .setFooter("× NoviaMC.DE » Clan-System", null)
                                    .setColor(Color.RED)
                    );

                    // Kick target from clan
                    clanPlayer.getClan().kick(target);
                    target.reload();
                    return;
                }

                // If it is not permitted
                player.closeInventory();
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "§cDu bist derzeit nicht berechtigt %s §caus dem Clan werfen zu können!",
                        target.getName()
                ));

            }

            case 35 -> {
                player.closeInventory();

                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                player.openInventory(new ClanMemberEditPage(this.clanPlugin)
                        .getInventory(clanPlayer, target));
            }

        }

    }

    private ClanPlayerObject getClanPlayerFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (itemStack.getItemMeta() == null) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;
        if (itemStack.getItemMeta().displayName() == null) return null;

        var itemName = PlainTextComponentSerializer.plainText()
                .serialize(Objects.requireNonNull(itemStack.getItemMeta().displayName()));

        var targetName = itemName.split("§8• §e")[1];
        if (targetName == null) return null;

        var targetUUID = UniqueIdManager.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayerObject.fromPlayer(targetUUID);
    }


}
