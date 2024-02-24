package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.ClanMemberEditPage;
import de.peaqe.latetimeclan.inventory.ClanMemberPage;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.UUIDFetcher;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanMemberEditPage.isPermitted(clanPlayer, target, ClanAction.KICK)) {

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

                    // Clan notify
                    // TODO: Global notify

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

        var targetUUID = UUIDFetcher.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayer.fromPlayer(targetUUID);
    }


}
