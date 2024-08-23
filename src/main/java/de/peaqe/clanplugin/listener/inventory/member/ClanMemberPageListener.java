package de.peaqe.clanplugin.listener.inventory.member;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.member.ClanMemberEditPage;
import de.peaqe.clanplugin.inventory.navigation.ClanInfoPage;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
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

public class ClanMemberPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanMemberPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Mitglieder"
                ))
        )) return;

        event.setCancelled(true);

        var currentItem = event.getCurrentItem();
        if (currentItem == null || !currentItem.hasItemMeta() || !currentItem.getItemMeta().hasDisplayName()) return;

        var clanPlayer = ClanPlayerObject.fromPlayer(player);
        if (clanPlayer == null) return;

        if (event.getSlot() == 44) {
            player.closeInventory();
            player.openInventory(new ClanInfoPage(this.clanPlugin, clanPlayer.getClan())
                    .getInventory(player));
            return;
        }

        var currentClanPlayer = this.getClanPlayerFromItemStack(currentItem);

        player.closeInventory();
        player.openInventory(new ClanMemberEditPage(this.clanPlugin)
                .getInventory(clanPlayer, currentClanPlayer));

    }

    private ClanPlayerObject getClanPlayerFromItemStack(ItemStack itemStack) {

        if (itemStack == null) return null;
        if (!itemStack.hasItemMeta()) return null;
        if (itemStack.getItemMeta() == null) return null;
        if (!itemStack.getItemMeta().hasDisplayName()) return null;
        if (itemStack.getItemMeta().displayName() == null) return null;

        var itemName = PlainTextComponentSerializer.plainText()
                .serialize(Objects.requireNonNull(itemStack.getItemMeta().displayName()));

        var targetName = itemName.split("§8• §a")[1];
        if (targetName == null) return null;

        var targetUUID = UniqueIdManager.getUUID(targetName);
        if (targetUUID == null) return null;

        return ClanPlayerObject.fromPlayer(targetUUID);
    }

}
