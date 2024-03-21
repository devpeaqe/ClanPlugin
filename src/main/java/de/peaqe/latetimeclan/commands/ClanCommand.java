package de.peaqe.latetimeclan.commands;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.navigation.ClanInfoPage;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.objects.*;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.ClanUtil;
import de.peaqe.latetimeclan.util.manager.InvitationManager;
import de.peaqe.latetimeclan.webhook.DiscordWebhook;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.02.2024 | 15:46 Uhr
 * *
 */

public class ClanCommand implements CommandExecutor, TabExecutor {

    private final LateTimeClan lateTimeClan;
    private final Messages messages;
    private final InvitationManager invitationManager;

    public ClanCommand(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        this.messages = lateTimeClan.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setTabCompleter(this);
        this.invitationManager = lateTimeClan.getInvitationManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;

        // /clan
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("info"))) {
            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            player.openInventory(new ClanInfoPage(this.lateTimeClan, clan).getInventory(player));
            return true;
        }

        // /clan leave
        if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());
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

            clan.kick(clanPlayer);

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
            player.sendMessage(this.messages.compileMessage(
                    "Du hast den Clan %s verlassen!",
                    clan.getName()
            ));

            clan.sendNotification(
                    "Das Mitglied %s hat den Clan §cverlassen§7.",
                    player.getName()
            );

            this.lateTimeClan.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan verlassen")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanPlayer.getClan().getName(), true)
                            .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                            .setFooter("× LateTimeMC.DE » Clan-System", null)
                            .setColor(Color.RED)
            );

            return true;
        }

        // /clan join <clanTag>
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {

            if (this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId()) != null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist bereits in einem Clan. Diesen musst du vorher verlassen! %s",
                        "/clan leave"
                ));
                return true;
            }

            var clanTag = args[1].toLowerCase();
            var clan = this.lateTimeClan.getClanDatabase().getClan(clanTag);

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

                player.sendMessage(this.messages.compileMessage(
                        "Du bist dem Clan %s beigetreten!",
                        clan.getName()
                ));

                player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan beigetreten")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor(Color.GREEN)
                );

                return true;
            }

            if (!(this.invitationManager.isClanJoinable(clan) &&
                    this.invitationManager.isInvited(player.getUniqueId(), clan.getTag()))) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan %s nimmt derzeit keine neuen Mitglieder auf!",
                        clan.getName()
                ));
                return true;
            }

            this.invitationManager.unInvite(player.getUniqueId(), clan);

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

            player.sendMessage(this.messages.compileMessage(
                    "Du bist dem Clan %s beigetreten!",
                    clan.getName()
            ));
            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);

            this.lateTimeClan.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan beigetreten")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanPlayer.getClan().getName(), true)
                            .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                            .setFooter("× LateTimeMC.DE » Clan-System", null)
                            .setColor(Color.GREEN)
            );

            return true;
        }

        // /clan invite <member>
        if (args.length == 2 && args[0].equalsIgnoreCase("invite")) {

            var clanModel = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

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

            // TODO: Add offline support
            var target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s konnte nicht gefunden werden oder ist derzeit nicht online!",
                        args[1]
                ));
                return true;
            }

            if (target.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(this.messages.compileMessage(
                        "Du kannst dich nicht selbst einladen!"
                ));
                return true;
            }

            if (this.lateTimeClan.getClanDatabase().getClanModelOfMember(target.getUniqueId()) != null) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s ist bereits in einem Clan!",
                        target.getName()
                ));
                return true;
            }

            if (this.invitationManager.isInvited(target.getUniqueId(), clanModel.getTag())) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Spieler %s hat bereits eine Einladung vom Clan erhalten!",
                        target.getName()
                ));
                return true;
            }

            if (this.invitationManager.invite(target.getUniqueId(), clanModel)) {
                target.sendMessage(this.messages.compileMessage(
                        "Du wurdest von dem Clan %s eingeladen.",
                        clanModel.getName()
                ));

                //player.sendMessage(this.messages.compileMessage(
                //        "Du hast den Spieler %s eingeladen.",
                //        target.getName()
                //));

                clanModel.sendNotification(
                        "Der Spieler %s wurde von %s eingeladen.",
                        target.getName(),
                        player.getName()
                );

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Clan Einladung")
                                .addField("Mitglied", target.getName(), true)
                                .addField("Eingeladen von", player.getName(), true)
                                .addField("Clan", clanModel.getName(), true)
                                .addField("Clan-Tag", clanModel.getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor(Color.YELLOW)
                );

            }

            return true;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("bank")) {
            
            var clanModel = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());
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
            
            player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                    "Der Clan besitzt derzeit %s auf der Bank.",
                    ClanUtil.compressInt(clanModel.getClanBankAmount()) + "§7€"
            ));
            
        }
        
        // /clan bank action <amount>
        if (args.length == 3 && args[0].equalsIgnoreCase("bank")) {

            var amount = (int) 0;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Die von dir angegebene %s ist ungültig!", "Zahl"
                ));
                return true;
            }

            if (amount <= 0) {
                player.sendMessage(this.lateTimeClan.getMessages().compileMessage(
                        "Die von dir angegebene %s muss im positivem Bereich liegen und größer als 0 sein!",
                        "Zahl"
                ));
                return true;
            }

            var clanModel = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());
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

                clanModel.setClanBankAmount(clanModel.getClanBankAmount() + amount);
                this.lateTimeClan.getClanDatabase().updateClan(clanModel);

                clanModel.sendNotification(
                        "Der Spieler %s hat %s in die %s eingezahlt.",
                        player.getName(),
                        ClanUtil.compressInt(amount) + "§7€",
                        "Clan-Bank"
                );

                // TODO: Add money system and remove them from the player

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Geld eingezahlt")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Anzahl", amount + "€", true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
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

                clanModel.setClanBankAmount(clanModel.getClanBankAmount() - amount);
                this.lateTimeClan.getClanDatabase().updateClan(clanModel);

                clanModel.sendNotification(
                        "Der Spieler %s hat %s aus der %s abgehoben.",
                        player.getName(),
                        ClanUtil.compressInt(amount) + "§7€",
                        "Clan-Bank"
                );

                // TODO: Add money system and add them to the player

                this.lateTimeClan.getWebhookSender().sendWebhook(
                        new DiscordWebhook.EmbedObject().setTitle("Geld abgehoben")
                                .addField("Mitglied", player.getName(), true)
                                .addField("Anzahl", amount + "€", true)
                                .addField("Clan", clanPlayer.getClan().getName(), true)
                                .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                                .setFooter("× LateTimeMC.DE » Clan-System", null)
                                .setColor(Color.RED)
                );


            }

        }

        // /clan create <name> <tag>
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {

            var clanName = args[1];
            var clanTag = args[2].toLowerCase();

            if (this.lateTimeClan.getClanDatabase().clanExists(clanTag)) {
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
                    ClanInvitationStatus.INVITATION,
                    10,
                    Map.of(
                            player.getUniqueId(), ClanGroup.OWNER
                    ),
                    new SettingsObject(true, false),
                    0
            );

            this.lateTimeClan.getClanDatabase().createClan(clanModel);

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
            player.sendMessage(this.messages.compileMessage(
                    "Du hast den Clan %s erfolgreich erstellt!",
                    clanTag
            ));

            this.lateTimeClan.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan erstellt")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Clan", clanModel.getName(), true)
                            .addField("Clan-Tag", clanModel.getTag(), true)
                            .setFooter("× LateTimeMC.DE » Clan-System", null)
                            .setColor(Color.YELLOW)
            );

            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        ArrayList<String> matches = new ArrayList<>();
        if (!(sender instanceof Player player)) return matches;
        var clanPlayer = ClanPlayerObject.fromPlayer(player);

        if (args.length == 1) {

            if (clanPlayer != null) {
                matches.add("info");
                matches.add("bank");
                if (clanPlayer.hasPermission(ClanAction.INVITE)) matches.add("invite");
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
        
        if (args.length == 2 && args[0].equalsIgnoreCase("bank")) {
            if (clanPlayer != null) {
                if (clanPlayer.hasPermission(ClanAction.BANK_REMOVE)) matches.add("remove");
                if (clanPlayer.hasPermission(ClanAction.BANK_ADD)) matches.add("add"); 
            }
        }
            
        if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
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
