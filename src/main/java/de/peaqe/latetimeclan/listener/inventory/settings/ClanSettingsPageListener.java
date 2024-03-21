package de.peaqe.latetimeclan.listener.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.inventory.settings.ClanSettingsChangeStatePage;
import de.peaqe.latetimeclan.inventory.settings.ClanSettingsModerateChatPage;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.manager.UniqueIdManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanSettingsPageListener implements Listener {

    private final LateTimeClan lateTimeClan;
    private final Map<UUID, ClanGroup> cache;

    public ClanSettingsPageListener(LateTimeClan lateTimeClan) {
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
                        "§8Einstellungen"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;

        switch (event.getSlot()) {

            case 29 -> {
                // Clan Status
                player.closeInventory();

                if (!clanPlayer.hasPermission(ClanAction.CHANGE_STATE)) {
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Du bist derzeit nicht berechtigt den %s zu ändern!",
                            "Clan-Status"
                    ));
                    return;
                }

                player.openInventory(new ClanSettingsChangeStatePage(this.lateTimeClan, clanPlayer.getClan())
                        .getInventory());
            }

            case 31 -> {

                // Clan Chat
                player.closeInventory();

                if (!clanPlayer.hasPermission(ClanAction.SETTINGS_MODERATE_CHAT)) {
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt den %s %s zu schalten!",
                            "Clan-Status",
                            "§aein§8-/§caus"
                    ));
                    return;
                }

                player.openInventory(new ClanSettingsModerateChatPage(this.lateTimeClan, clanPlayer.getClan())
                        .getInventory());

            }

            case 33 -> {
                player.closeInventory();
                player.openInventory(new ClanInfoPage(this.lateTimeClan, clanPlayer.getClan()).getInventory(player));
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

    public Map<UUID, ClanGroup> getCache() {
        return cache;
    }
}
