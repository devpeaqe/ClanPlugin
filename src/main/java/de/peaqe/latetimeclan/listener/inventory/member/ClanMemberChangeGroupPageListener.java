package de.peaqe.latetimeclan.listener.inventory.member;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.member.ClanMemberChangeGroupConfirmPage;
import de.peaqe.latetimeclan.inventory.member.ClanMemberEditPage;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.manager.UniqueIdManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanMemberChangeGroupPageListener implements Listener {

    private final LateTimeClan lateTimeClan;
    private final Map<UUID, ClanGroup> cache;

    public ClanMemberChangeGroupPageListener(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        Bukkit.getPluginManager().registerEvents(this, this.lateTimeClan);
        this.cache = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.lateTimeClan.getMessages().compileMessage(
                        "§8Gruppe bearbeiten"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;
        if (!clanPlayer.hasPermission(ClanAction.CHANGE_GROUP)) return;

        var target = this.getClanPlayerFromItemStack(event.getClickedInventory().getItem(13));

        if (target == null) return;
        if (!ClanUtil.isPermitted(clanPlayer, target, ClanAction.CHANGE_GROUP)) return;

        switch (event.getSlot()) {

            case 29 -> {
                if (target.getClanGroup().equals(ClanGroup.MEMBER)) return;

                //target.setClanGroup(ClanGroup.MEMBER);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroup.MEMBER);

                //clanPlayer.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan)
                        .getInventory(clanPlayer, target, ClanGroup.MEMBER));

            }

            case 31 -> {
                if (target.getClanGroup().equals(ClanGroup.MODERATOR)) return;

                //target.setClanGroup(ClanGroup.MODERATOR);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroup.MODERATOR);

               //target.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan)
                        .getInventory(clanPlayer, target, ClanGroup.MODERATOR));
            }

            case 33 -> {
                if (target.getClanGroup().equals(ClanGroup.MANAGER)) return;

                //target.setClanGroup(ClanGroup.MANAGER);

                player.closeInventory();
                this.cache.put(target.getUniqueId(), ClanGroup.MANAGER);

                //target.getClan().reload();
                player.openInventory(new ClanMemberChangeGroupConfirmPage(this.lateTimeClan)
                        .getInventory(clanPlayer, target, ClanGroup.MANAGER));

            }

            case 44 -> {
                // GO BACK
                player.closeInventory();
                player.openInventory(new ClanMemberEditPage(this.lateTimeClan)
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

    public Map<UUID, ClanGroup> getCache() {
        return cache;
    }
}
