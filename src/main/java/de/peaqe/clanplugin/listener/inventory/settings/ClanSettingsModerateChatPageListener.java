package de.peaqe.clanplugin.listener.inventory.settings;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.navigation.ClanInfoPage;
import de.peaqe.clanplugin.inventory.settings.ClanSettingsPage;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.02.2024 | 17:05 Uhr
 * *
 */

public class ClanSettingsModerateChatPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanSettingsModerateChatPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Chat verwalten"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;

        switch (event.getSlot()) {

            case 20, 24 -> {

                player.closeInventory();
                if (event.getCurrentItem() == null) return;

                var clanChatToggled = this.getClanChatToggledFromItemStack(event.getCurrentItem());
                if (!clanPlayer.hasPermission(ClanAction.SETTINGS_MODERATE_CHAT)) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt den %s zu ändern!",
                            "Clan-Status"
                    ));
                    return;
                }

                // TODO: Find way
                //if (clanPlayer.getClan().getSettings().isClanChatToggled() == clanChatToggled) {
                //    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                //            "Der Clan-Chat ist bereits %s!",
                //            (clanPlayer.getClan().getSettings().isClanChatToggled() ? "§aaktiviert" : "§cdeaktiviert")
                //    ));
                //    return;
                //}

                //player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                //        "Der Clan-Chat wurde %s!",
                //        (clanChatToggled ? "§aaktiviert" : "§cdeaktiviert")
                //));

                clanPlayer.getClan().getSettings().setClanChatToggled(clanChatToggled);
                clanPlayer.getClan().update();

                clanPlayer.getClan().sendNotification(
                        "Der %s wurde von %s %s.",
                        "Clan-Chat",
                        player.getName(),
                        (clanChatToggled ? "§aaktiviert" : "§cdeaktiviert")
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan-Chat " +
                                        (clanChatToggled ? "aktiviert" : "deaktiviert"))
                                .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setDescription("Der Clan-Chat wurde von " + player.getName() +
                                        (clanChatToggled ? " aktiviert" : " deaktiviert") + ".")
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor((clanChatToggled ? Color.GREEN : Color.RED))
                );

                player.openInventory(new ClanInfoPage(this.clanPlugin, clanPlayer.getClan()).getInventory(player));

            }

            case 35 -> {
                player.closeInventory();
                player.openInventory(new ClanSettingsPage(this.clanPlugin, clanPlayer.getClan()).getInventory());
            }

        }
    }

    @SuppressWarnings(value = "deprecation")
    private boolean getClanChatToggledFromItemStack(@NotNull ItemStack itemStack) {
        return (itemStack.getType().equals(Material.GREEN_DYE) &&
                itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§8• §aAktivieren"));

    }
}
