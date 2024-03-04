package de.peaqe.latetimeclan.listener;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.ClanMemberChangeGroupPage;
import de.peaqe.latetimeclan.inventory.ClanMemberEditPage;
import de.peaqe.latetimeclan.inventory.ClanMemberKickConfirmPage;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import de.peaqe.latetimeclan.util.uuid.UUIDFetcher;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

public class ClanMemberEditPageListener implements Listener {

    private final LateTimeClan lateTimeClan;

    public ClanMemberEditPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Mitglieder verwalten"
                ))
        )) return;

        event.setCancelled(true);

        switch (event.getSlot()) {

            case 20 -> {

                // KICK
                var clanPlayer = ClanPlayer.fromPlayer(player);

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanMemberEditPage.isPermitted(clanPlayer, target, ClanAction.KICK)) {

                    player.closeInventory();
                    player.openInventory(new ClanMemberKickConfirmPage(this.lateTimeClan, clanPlayer.getClan()).getInventory(
                            clanPlayer, target
                    ));

                    /*
                    clanPlayer.getClan().kick(target);

                    player.closeInventory();
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                            "Der Spieler %s wurde erfolgreich aus deinem Clan geschmissen!",
                            target.getName()
                    ));

                    var targetPlayer = Bukkit.getPlayer(target.getUniqueId());
                    if (targetPlayer != null) {
                        targetPlayer.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                                "Du wurdest aus dem Clan %s geschmissen!",
                                clanPlayer.getClan().getName()
                        ));
                    }
                     */

                } else {

                    player.closeInventory();
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                            "Du hast nicht die benötigte Berechtigung um %s aus dem Clan zu werfen!",
                            target.getName()
                    ));
                }
            }

            case 24 -> {

                // CHANGE GROUP
                var clanPlayer = ClanPlayer.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (clanPlayer.hasPermission(ClanAction.CHANGE_GROUP)) {
                    player.closeInventory();
                    player.openInventory(new ClanMemberChangeGroupPage(this.lateTimeClan, clanPlayer.getClan())
                            .getInventory(clanPlayer, target));
                    return;
                }

                player.closeInventory();
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Du hast nicht die benötigte Berechtigung um die Gruppe von %s zu bearbeiten!",
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
