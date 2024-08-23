package de.peaqe.clanplugin.listener.inventory.member;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.member.ClanMemberChangeGroupPage;
import de.peaqe.clanplugin.inventory.member.ClanMemberKickConfirmPage;
import de.peaqe.clanplugin.inventory.member.ClanMemberPage;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.util.manager.UniqueIdManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanMemberEditPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanMemberEditPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
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
                    player.openInventory(new ClanMemberKickConfirmPage(this.clanPlugin).getInventory(
                            clanPlayer, target
                    ));
                    return;
                }

                player.closeInventory();
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
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
                    player.openInventory(new ClanMemberChangeGroupPage(this.clanPlugin)
                            .getInventory(target));
                    return;
                }

                player.closeInventory();
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Du hast nicht die benötigte Berechtigung um die Gruppe von %s zu bearbeiten!",
                        target.getName()
                ));
            }

            case 35 -> {
                // GO BACK
                player.closeInventory();

                var clanPlayer = ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) return;

                player.openInventory(new ClanMemberPage(this.clanPlugin, clanPlayer.getClan()).getInventory());
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
