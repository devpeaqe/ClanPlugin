package de.peaqe.clanplugin.commands;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.inventory.deletion.ClanDeleteConfirmPage;
import de.peaqe.clanplugin.inventory.navigation.ClanInfoPage;
import de.peaqe.clanplugin.messages.Messages;
import de.peaqe.clanplugin.objects.*;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.util.ClanUtil;
import de.peaqe.clanplugin.util.color.Hex;
import de.peaqe.clanplugin.util.manager.InvitationManager;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.02.2024 | 15:46 Uhr
 * *
 */

public class ClanCommand implements CommandExecutor, TabExecutor {

    private final ClanPlugin clanPlugin;
    private final Messages messages;
    private final InvitationManager invitationManager;

    public ClanCommand(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        this.messages = clanPlugin.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setTabCompleter(this);
        this.invitationManager = clanPlugin.getInvitationManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;

        // /clan
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("info"))) {
            var clan = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            player.openInventory(new ClanInfoPage(this.clanPlugin, clan).getInventory(player));
            return true;
        }

        // /clan leave
        if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {

            var clan = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());
            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayer = ClanPlayerObject.fromPlayer(player);
            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "§cEs ist ein Fehler aufgetreten! Bitte wende dich an einen Administrator."
                ));
                return true;
            }

            if (clanPlayer.getClanGroup().equals(ClanGroup.OWNER)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du kannst dein eigenen %s nicht verlassen!",
                        "Clan"
                ));
                return true;
            }

            this.clanPlugin.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan verlassen")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanPlayer.getClan().getName(), true)
                            .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                            .setFooter("× NoviaMC.DE » Clan-System", null)
                            .setColor(Color.RED)
            );

            clan.kick(clanPlayer);
            clanPlayer.setNull();

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
            player.sendMessage(this.messages.compileMessage(
                    "Du hast den Clan %s verlassen!",
                    clan.getName()
            ));

            clan.sendNotification(
                    "Das Mitglied %s hat den Clan §cverlassen§7.",
                    player.getName()
            );

            return true;
        }

        // /clan delete
        if (args.length == 1 && args[0].equalsIgnoreCase("delete")) {

            var clan = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());
            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayer = ClanPlayerObject.fromPlayer(player);
            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "§cEs ist ein Fehler aufgetreten! Bitte wende dich an einen Administrator."
                ));
                return true;
            }

            if (!clanPlayer.hasPermission(ClanAction.DELETE)) {
                player.sendMessage(this.messages.compileMessage(
                        "§cDu hast keine Berechtigung diesen Clan zu löschen!"
                ));
                return true;
            }

            player.openInventory(new ClanDeleteConfirmPage(this.clanPlugin, clan).getInventory());
            return true;

        }

        // /clan join <clanTag>
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {

            if (this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId()) != null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist bereits in einem Clan. Diesen musst du vorher verlassen! %s",
                        "/clan leave"
                ));
                return true;
            }

            var clanTag = args[1].toLowerCase();
            var optionalClan = this.clanPlugin.getClanDatabase().getClan(clanTag);
            var clan = (ClanObject) null;

            if (optionalClan.isPresent()) clan = optionalClan.get();

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan %s existiert nicht!",
                        clanTag
                ));
                return true;
            }

            if (clan.getClanInvitationStatus().equals(ClanInvitationStatus.OPEN) &&
                    clan.getMembers().size() < clan.getMaxSize()) {

                var clanPlayer = new ClanPlayerObject(
                        player.getName(),
                        player.getUniqueId(),
                        clan,
                        ClanGroup.MEMBER
                );

                if (ClanPlayerObject.fromPlayer(player) != null) clanPlayer =  ClanPlayerObject.fromPlayer(player);
                if (clanPlayer == null) {
                    player.sendMessage(this.messages.compileMessage(
                            "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                    ));
                    player.sendMessage(this.messages.compileMessage(
                            "Discord » %s", "https://discord.gg/USHsrPjU8J"
                    ));
                    return true;
                }

                clan.sendNotification(
                        "%s ist dem Clan beigetreten, heißen wir unser neues Mitglied willkommen!",
                        player.getName()
                );
                
                clan.addMember(clanPlayer);
                this.clanPlugin.getInvitationManager().unInvite(player.getUniqueId(), clan);

                player.sendMessage(this.messages.compileMessage(
                        "Du bist dem Clan %s beigetreten!",
                        clan.getName()
                ));

                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan beigetreten")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.GREEN)
                );

                return true;
            }

            if (!(this.invitationManager.isClanJoinable(clan) &&
                    this.invitationManager.isInvited(player.getUniqueId(), clan))) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan %s nimmt derzeit keine neuen Mitglieder auf!",
                        clan.getName()
                ));
                return true;
            }

            var clanPlayer = new ClanPlayerObject(
                    player.getName(),
                    player.getUniqueId(),
                    clan,
                    ClanGroup.MEMBER
            );

            if (ClanPlayerObject.fromPlayer(player) != null) clanPlayer =  ClanPlayerObject.fromPlayer(player);

            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                ));
                player.sendMessage(this.messages.compileMessage(
                        "Discord » %s", "https://discord.gg/USHsrPjU8J"
                ));
                return true;
            }

            clan.sendNotification(
                    "%s ist dem Clan beigetreten, heißen wir unser neues Mitglied willkommen!",
                    player.getName()
            );

            clan.addMember(clanPlayer);
            this.clanPlugin.getInvitationManager().unInvite(player.getUniqueId(), clan);

            player.sendMessage(this.messages.compileMessage(
                    "Du bist dem Clan %s beigetreten!",
                    clan.getName()
            ));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);

            this.clanPlugin.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan beigetreten")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanPlayer.getClan().getName(), true)
                            .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                            .setFooter("× NoviaMC.DE » Clan-System", null)
                            .setColor(Color.GREEN)
            );

            return true;
        }

        // /clan decline <clan-tag>
        if (args.length == 2 && args[0].equalsIgnoreCase("decline")) {

            var clanTag = args[1].toLowerCase();
            var optionalClan = this.clanPlugin.getClanDatabase().getClan(clanTag);
            var clan = (ClanObject) null;

            if (optionalClan.isPresent()) clan = optionalClan.get();

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan %s existiert nicht!",
                        clanTag
                ));
                return true;
            }

            if (!this.invitationManager.isInvited(player.getUniqueId(), clan)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du wurdest von dem Clan %s nicht eingeladen.",
                        clan.getName()
                ));
                return true;
            }

            this.invitationManager.unInvite(player.getUniqueId(), clan);

            player.sendMessage(this.messages.compileMessage(
                    "Du hast die %s von dem Clan %s abgelehnt.",
                    "Einladung", clan.getName()
            ));

            return true;
        }

        // /clan invite <member>
        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {

            var clanModel = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            if (clanModel == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayerSender = ClanPlayerObject.fromPlayer(player);

            if (clanPlayerSender == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                ));
                player.sendMessage(this.messages.compileMessage(
                        "Discord » %s", "https://discord.gg/USHsrPjU8J"
                ));
                return true;
            }

            if (!clanPlayerSender.hasPermission(ClanAction.INVITE)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit nicht dazu berechtigt %s einladen zu können!",
                        "Mitglieder"
                ));
                return true;
            }

            if (!this.invitationManager.isClanJoinable(clanModel)) {
                player.sendMessage(this.messages.compileMessage(
                        "Dein Clan ist derzeit voll oder die Einladungen wurden pausiert!"
                ));
                return true;
            }
            
            var targetUUID = this.clanPlugin.getPlayerDatabase().getUniqueId(args[1]).orElse(null);
            if (targetUUID == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s konnte nicht gefunden werden!",
                        args[1]
                ));
                return true;
            }
            
            var targetName = this.clanPlugin.getPlayerDatabase().getName(targetUUID);
            if (targetName == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s konnte nicht gefunden werden!",
                        args[1]
                ));
                return true;
            }

            if (targetName.equalsIgnoreCase(player.getName())) {
                player.sendMessage(this.messages.compileMessage(
                        "Du kannst dich nicht selbst einladen!"
                ));
                return true;
            }

            if (this.clanPlugin.getClanDatabase().getClanModelOfMember(targetUUID) != null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s ist bereits in einem Clan!",
                        targetName
                ));
                return true;
            }

            if (this.invitationManager.isInvited(targetUUID, clanModel)) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s hat bereits eine Einladung vom Clan erhalten!",
                        targetName
                ));
                return true;
            }

            if (this.invitationManager.invite(targetUUID, clanModel)) {

                var target = Bukkit.getPlayer(targetUUID);
                if (target != null) {
                    var message = Component.text(this.messages.compileMessage("", "")).asComponent();

                    var accept = Component.text("§8[§aAnnehmen§8]").asComponent();
                    accept = accept.hoverEvent(HoverEvent.showText(
                            Component.text("§7Akzeptiere die §eEinladung§7.")));
                    accept = accept.clickEvent(ClickEvent.runCommand("/clan join " + clanModel.getTag()));

                    var decline = Component.text("§8[§cAblehnen§8]").asComponent();
                    decline = decline.hoverEvent(HoverEvent.showText(
                            Component.text("§7Lehne die §eEinladung§7 ab.")));
                    decline = decline.clickEvent(ClickEvent.runCommand("/clan decline " + clanModel.getTag()));

                    message = message.append(accept);
                    message = message.append(Component.space());
                    message = message.append(decline);

                    target.sendMessage(this.messages.compileMessage(
                            "Du wurdest von dem Clan %s eingeladen.",
                            clanModel.getName()
                    ));
                    target.sendMessage(message);
                }

                clanModel.sendNotification(
                        "Der Spieler %s wurde von %s eingeladen.",
                        targetName,
                        player.getName()
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan Einladung")
                                .addField("Mitglied", targetName, true)
                                .addField("Eingeladen von", player.getName(), true)
                                .addField("Clan", clanModel.getName(), true)
                                .addField("Clan-Tag", clanModel.getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.YELLOW)
                );

            }

            return true;
        }

        // /clan bank
        if (args.length == 1 && args[0].equalsIgnoreCase("bank")) {
            
            var clanModel = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());
            if (clanModel == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayer = ClanPlayerObject.fromPlayer(player);
            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                ));
                return true;
            }
            
            player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                    "Der Clan besitzt derzeit $%s auf der Bank.",
                    ClanUtil.compressInt(clanModel.getClanBankAmount())
            ));

            return true;
        }

        // /clan color <color>
        if (args.length == 2 && args[0].equalsIgnoreCase("color")) {

            var clanModel = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            if (clanModel == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayerSender = ClanPlayerObject.fromPlayer(player);

            if (clanPlayerSender == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                ));
                player.sendMessage(this.messages.compileMessage(
                        "Discord » %s", "https://discord.gg/USHsrPjU8J"
                ));
                return true;
            }

            if (!clanPlayerSender.hasPermission(ClanAction.CHANGE_COLOR)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit nicht berechtigt die Farbe des %s ändern zu können!",
                        "Clan-Tags"
                ));
                return true;
            }

            var hex = args[1];

            if (hex.length() != 7) {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Die von dir angegebene %s ist ungültig!", "Hex-Code"
                ));
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Gebe den %s in folgendem Format an: #%s",
                        "Hex-Code", "§eAABBCC"
                ));
                return true;
            }

            if (hex.charAt(0) != '#') {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Die von dir angegebene %s ist ungültig!", "Hex-Code"
                ));
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Gebe den %s in folgendem Format an: #%s",
                        "Hex-Code", "§eAABBCC"
                ));
                return true;
            }

            if (hex.replace("#", "").equalsIgnoreCase(clanModel.getColor())) {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Du hast diese %s des %s bereits angegeben!",
                        "Farbe", "Clan-Tags"
                ));
                return true;
            }

            try {

                var hexColor = Hex.color(hex.replace("#", ""));
                var tmpColor = Hex.color(clanModel.getColor());

                clanModel.setColor(hex.replace("#", ""));
                this.clanPlugin.getClanDatabase().updateClan(clanModel);

                clanModel.sendNotification(
                        "Die Farbe des %s wurde geändert.",
                        "Clan-Tags"
                );

                clanModel.sendNotification(
                        "Vorher: %s", tmpColor + clanModel.getTag()
                );

                clanModel.sendNotification(
                        "Nachher: %s", hexColor + clanModel.getTag()
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan-Tag Farbe geändert")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Alte Farbe", tmpColor, true)
                                .addField("Neue Farbe", hexColor, true)
                                .addField("Clan", clanModel.getName(), true)
                                .addField("Clan-Tag", clanModel.getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.ORANGE)
                );

            } catch (IllegalArgumentException e) {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Du hast einen §cungültigen %s angegeben! Bitte versuche einen anderen.",
                        "Hex-Code"
                ));
                return true;
            }

            return true;
        }
        
        // /clan bank action <amount>
        if (args.length == 3 && args[0].equalsIgnoreCase("bank")) {

            var amount = 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Die von dir angegebene %s ist ungültig!", "Zahl"
                ));
                return true;
            }

            if (amount <= 0) {
                player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                        "Die von dir angegebene %s muss im positivem Bereich liegen und größer als %s sein!",
                        "Zahl", "§c0"
                ));
                return true;
            }

            var clanModel = this.clanPlugin.getClanDatabase().getClanModelOfMember(player.getUniqueId());
            if (clanModel == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayer = ClanPlayerObject.fromPlayer(player);
            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Es ist ein Fehler aufgetreten! Bitte wende dich an unseren Support."
                ));
                return true;
            }

            // /clan bank add <amount>
            if (args[1].equalsIgnoreCase("add")) {

                if (!clanPlayer.hasPermission(ClanAction.BANK_ADD)) {
                    player.sendMessage(this.messages.compileMessage(
                            "Du bist derzeit nicht berechtigt in die %s einzuzahlen!",
                            "Clan-Bank"
                    ));
                    return true;
                }

                if (clanModel.getClanBankAmount() >= 5000000) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Die %s ist derzeit voll.", "Clan-Bank"
                    ));
                    return true;
                }

                if (clanModel.getClanBankAmount() + amount > 5000000) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Die %s kann nicht mehr als $%s betragen.",
                            "Clan-Bank",
                            ClanUtil.compressInt(5000000)
                    ));
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Aktueller Kontostand: $%s.",
                            ClanUtil.compressInt(clanModel.getClanBankAmount())
                    ));
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Maximaler Betrag zum einzahlen: $%s.",
                            ClanUtil.compressInt((clanModel.getClanBankAmount() - 5000000) * -1)
                    ));
                    return true;
                }

                if (this.clanPlugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du besitzt derzeit nicht genügend Guthaben um dies auf die %s einzuzahlen.",
                            "Clan-Bank"
                    ));
                    return true;
                }

                // Remove money from the player
                var response = this.clanPlugin.getEconomy().withdrawPlayer(player, amount);

                if (!response.transactionSuccess()) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Bei der Zahlung ist ein Fehler aufgetreten. Bitte versuche es später erneut."
                    ));
                    return true;
                }

                clanModel.setClanBankAmount(clanModel.getClanBankAmount() + amount);
                this.clanPlugin.getClanDatabase().updateClan(clanModel);

                clanModel.sendNotification(
                        "Der Spieler %s hat $%s in die %s eingezahlt.",
                        player.getName(),
                        ClanUtil.compressInt(amount),
                        "Clan-Bank"
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Geld eingezahlt")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Anzahl",
                                        ClanUtil.compressIntWithoutColor(amount), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.GREEN)
                );

            }

            // /clan bank add <amount>
            if (args[1].equalsIgnoreCase("remove")) {

                if (!clanPlayer.hasPermission(ClanAction.BANK_REMOVE)) {
                    player.sendMessage(this.messages.compileMessage(
                            "Du bist derzeit nicht berechtigt in die %s einzuzahlen!",
                            "Clan-Bank"
                    ));
                    return true;
                }

                if (clanModel.getClanBankAmount() < amount) {
                    player.sendMessage(this.messages.compileMessage(
                            "Die %s besitzt derzeit nicht genug Geld.", "Clan-Bank"
                    ));
                    return true;
                }

                if (this.clanPlugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du besitzt derzeit nicht genügend Guthaben um dies auf die %s einzuzahlen.",
                            "Clan-Bank"
                    ));
                    return true;
                }

                // Give the money from the player
                var response = this.clanPlugin.getEconomy().depositPlayer(player, amount);

                if (!response.transactionSuccess()) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Bei der Zahlung ist ein Fehler aufgetreten. Bitte versuche es später erneut."
                    ));
                    return true;
                }

                clanModel.setClanBankAmount(clanModel.getClanBankAmount() - amount);
                this.clanPlugin.getClanDatabase().updateClan(clanModel);

                clanModel.sendNotification(
                        "Der Spieler %s hat $%s aus der %s abgehoben.",
                        player.getName(),
                        ClanUtil.compressInt(amount),
                        "Clan-Bank"
                );

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Geld abgehoben")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Anzahl",
                                        "$" + ClanUtil.compressIntWithoutColor(amount), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );


            }

            return true;
        }

        // /clan create <name> <tag>
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {

            var clanName = args[1];
            var clanTag = args[2].toUpperCase();
            var amount = 25000;

            if (clanTag.length() > 4) {
                player.sendMessage(this.messages.compileMessage(
                        "Der %s darf %s 4 Zeichen lang sein!",
                        "Clan-Tag", "§cmaximal"
                ));
                return true;
            }

            if (ClanUtil.isBlockedChar(clanName) != null) {
                player.sendMessage(this.messages.compileMessage(
                    "Du verwendest ein %s Zeichen im %s! %s",
                        "§cverbotenes", "§eClan-Name", "§8(§c" + ClanUtil.isBlockedChar(clanName) + "§8)"
                ));

                if (this.clanPlugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Du besitzt derzeit nicht genügend Guthaben um ein %s zu erstellen.",
                            "Clan"
                    ));
                    return true;
                }

                // Take money from the player
                var response = this.clanPlugin.getEconomy().depositPlayer(player, amount);

                if (!response.transactionSuccess()) {
                    player.sendMessage(this.clanPlugin.getMessages().compileMessage(
                            "Bei der Zahlung ist ein Fehler aufgetreten. Bitte versuche es später erneut."
                    ));
                    return true;
                }

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("BADWORD DETECTED")
                                .addField("Aktion", "Clan erstellung (Clan-Name)", true)
                                .addField("Nutzer", player.getName(), true)
                                .addField("UniqueId", player.getUniqueId().toString(), true)
                                .addField("Angegebenes Wort", clanName, true)
                                .addField("Überprüftes Wort", ClanUtil.getCheckedWort(clanName), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );
                return true;
            }

            if (ClanUtil.isBlockedChar(clanTag) != null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du verwendest ein %s Zeichen im %s! %s",
                        "§cverbotenes", "§eClan-Tag", "§8(§c" + ClanUtil.isBlockedChar(clanTag) + "§8)"
                ));

                this.clanPlugin.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("BADWORD DETECTED")
                                .addField("Aktion", "Clan erstellung (Clan-Tag)", true)
                                .addField("Nutzer", player.getName(), true)
                                .addField("Angegebenes Wort", clanTag, true)
                                .addField("Überprüftes Wort", ClanUtil.getCheckedWort(clanTag), true)
                                .setFooter("× NoviaMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );
                return true;
            }


            if (this.clanPlugin.getClanDatabase().clanExists(clanTag)) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan %s wurde bereits von einem anderen Spieler erstellt!",
                        clanTag
                ));
                return true;
            }

            var clanModel = new ClanObject(
                    clanName,
                    clanTag,
                    player.getUniqueId().toString(),
                    "FFEF66", // light yellow
                    ClanInvitationStatus.INVITATION,
                    10,
                    Map.of(
                            player.getUniqueId(), ClanGroup.OWNER
                    ),
                    new SettingsObject(true, false),
                    0,
                    new Date(System.currentTimeMillis() +
                            (TimeZone.getTimeZone("Europe/Berlin").getRawOffset() - (60 * 60 * 1000)))
            );

            this.clanPlugin.getClanDatabase().createClan(clanModel);

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
            player.sendMessage(this.messages.compileMessage(
                    "Du hast den Clan %s erfolgreich erstellt!",
                    clanTag
            ));

            this.clanPlugin.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan erstellt")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanModel.getName(), true)
                            .addField("Clan-Tag", clanModel.getTag(), true)
                            .setFooter("× NoviaMC.DE » Clan-System", null)
                            .setColor(Color.YELLOW)
            );

            return true;
        }

        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan create §8(§eClan-Name§8) (§eClan-Tag§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan bank einzahlen §8(§eBetrag§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan bank auszahlen §8(§eBetrag§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan invite §8(§eSpieler§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan color §8(§e#HEX§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan join (§eClan-Tag§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan decline (§eClan-Tag§8)"
        ));
        sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan leave"
        ));sender.sendMessage(this.messages.compileMessage(
                "Bitte verwende: §8/%s.", "clan info"
        ));

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                                @NotNull String label, @NotNull String[] args) {

        ArrayList<String> matches = new ArrayList<>();
        if (!(sender instanceof Player player)) return matches;
        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (args.length == 1) {

            if (clanPlayer != null) {
                matches.add("info");
                matches.add("bank");
                if (clanPlayer.hasPermission(ClanAction.INVITE)) matches.add("invite");
                if (clanPlayer.hasPermission(ClanAction.CHANGE_COLOR)) matches.add("color");
                if (!clanPlayer.getClanGroup().equals(ClanGroup.OWNER)) matches.add("leave");
                return matches;
            }

            matches.add("create");
            matches.add("join");

        }

        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            if (clanPlayer != null && clanPlayer.hasPermission(ClanAction.INVITE)) {
                Bukkit.getOnlinePlayers().forEach(target -> {

                    var clanPlayerTarget = ClanPlayerObject.fromPlayer(target);
                    if (clanPlayerTarget != null) return;

                    var input = args[1];

                    if (target.getName().toLowerCase().startsWith(input.toLowerCase())) {
                        matches.add(target.getName());
                    }

                });

                return matches;
            }
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("color")) {
            if (!(clanPlayer != null && clanPlayer.hasPermission(ClanAction.CHANGE_COLOR))) return matches;
            matches.add("#");
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("bank")) {
            if (clanPlayer != null) {
                if (clanPlayer.hasPermission(ClanAction.BANK_REMOVE)) matches.add("remove");
                if (clanPlayer.hasPermission(ClanAction.BANK_ADD)) matches.add("add"); 
            }
        }
            
        if (args.length == 2 && (args[0].equalsIgnoreCase("join") ||
                args[0].equalsIgnoreCase("decline"))) {
            if (this.invitationManager.getInvitations(player.getUniqueId()) != null &&
                    !this.invitationManager.getInvitations(player.getUniqueId()).isEmpty()) {
                this.invitationManager.getInvitations(player.getUniqueId()).forEach(clanTag -> {
                    if (clanTag.toLowerCase().startsWith(args[1].toLowerCase())) {
                        matches.add(clanTag);
                    }
                });
            }
        }

        return matches;
    }

}
