package de.peaqe.latetimeclan.listener.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberChangeGroupPage;
import de.peaqe.latetimeclan.inventory.member.ClanMemberKickConfirmPage;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.manager.UniqueIdManager;
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
                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanUtil.isPermitted(clanPlayer, target, ClanAction.KICK)) {

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

                    return;
                }

                player.closeInventory();
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Du hast nicht die benötigte Berechtigung um %s aus dem Clan zu werfen!",
                        target.getName()
                ));
            }

            case 24 -> {

                // CHANGE GROUP
                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));
                if (target == null) return;

                if (ClanUtil.isPermitted(clanPlayer, target, ClanAction.CHANGE_GROUP)) {
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
