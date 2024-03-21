package de.peaqe.latetimeclan.listener.inventory.settings;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.webhook.DiscordWebhook;
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

public class ClanSettingsToggleBankPageListener implements Listener {

    private final LateTimeClan lateTimeClan;
    private final Map<UUID, ClanGroup> cache;

    public ClanSettingsToggleBankPageListener(LateTimeClan lateTimeClan) {
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
                        "§8Bank Sichtbarkeit verwalten"
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
                    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                            "Du bist derzeit nicht berechtigt die %s zu verwalten!",
                            "Clan-Bank"
                    ));
                    return;
                }

                //if (clanPlayer.getClan().getSettings().isClanBankToggled() == clanBankToggled) {
                //    player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                //            "Die Clan-Bank ist bereits %s!",
                //            (clanPlayer.getClan().getSettings().isClanBankToggled() ? "§aaktiviert" : "§cdeaktiviert")
                //    ));
                //    return;
                //}

                //player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
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

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan-Bank " +
                                        (clanBankToggled ? "aktiviert" : "deaktiviert"))
                                .addField(clanPlayer.getClanGroup().getName(), player.getName(), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setDescription("Die Clan-Bank wurde von " + player.getName() +
                                        (clanBankToggled ? " aktiviert" : " deaktiviert") + ".")
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor((clanBankToggled ? Color.GREEN : Color.RED))
                );

                player.openInventory(new ClanInfoPage(this.lateTimeClan, clanPlayer.getClan()).getInventory(player));

            }
        }
    }

    @SuppressWarnings(value = "deprecation")
    private boolean getClanBankToggledFromItemStack(@NotNull ItemStack itemStack) {
        return (itemStack.getType().equals(Material.GREEN_DYE) &&
                itemStack.getItemMeta().getDisplayName().equalsIgnoreCase("§8• §aAktivieren"));

    }
}
