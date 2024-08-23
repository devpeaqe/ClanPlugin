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

public class ClanSettingsToggleBankPageListener implements Listener {

    private final ClanPlugin clanPlugin;

    public ClanSettingsToggleBankPageListener(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        Bukkit.getPluginManager().registerEvents(this, this.clanPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (!Component.text(event.getView().getOriginalTitle()).equals(
                Component.text(this.clanPlugin.getMessages().compileMessage(
                        "§8Bank verwalten"
                ))
        )) return;

        event.setCancelled(true);

        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (clanPlayer == null) return;

        switch (event.getSlot()) {

            case 20, 24 -> {

                player.closeInventory();
                if (event.getCurrentItem() == null) return;

                var clanBankToggled = this.getClanBankToggledFromItemStack(event.getCurrentItem());
                if (!clanPlayer.hasPermission(ClanAction.SETTINGS_BANK_VIEW)) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt die %s zu verwalten!",
                            "Clan-Bank"
                    ));
                    return;
                }

                // TODO: Find way
                //if (clanPlayer.getClan().getSettings().isClanBankToggled() == clanBankToggled) {
                //    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                //            "Die Clan-Bank ist bereits %s!",
                //            (clanPlayer.getClan().getSettings().isClanBankToggled() ? "§aaktiviert" : "§cdeaktiviert")
                //    ));
                //    return;
                //}

                //player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                //        "Die Clan-Bank wurde %s!",
                //        (clanBankToggled ? "§aaktiviert" : "§cdeaktiviert")
                //));

                clanPlayer.getClan().getSettings().setClanBankToggled(clanBankToggled);
                clanPlayer.getClan().update();

                clanPlayer.getClan().sendNotification(
                        "Die %s ist nun durch %s nun für %s sichtbar.",
                        "Clan-Bank",
                        player.getName(),
                        (clanBankToggled ? "§ajeden" : "§cClan-Mitglieder")
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan-Bank " +
                                        (clanBankToggled ? "aktiviert" : "deaktiviert"))
                                .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setDescription("Die Clan-Bank wurde von " + player.getName() +
                                        (clanBankToggled ? " aktiviert" : " deaktiviert") + ".")
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor((clanBankToggled ? Color.GREEN : Color.RED))
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
    private boolean getClanBankToggledFromItemStack(@NotNull ItemStack itemStack) {
        return (itemStack.getType().equals(Material.GREEN_DYE) &&
                itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§8• §aAktivieren"));

    }
}
