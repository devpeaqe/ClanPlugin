package de.peaqe.latetimeclan.commands;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.inventory.ClanInfoPage;
import de.peaqe.latetimeclan.inventory.ClanMemberPage;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanDecoder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private final LateTimeClan lateTimeClan;
    private final Messages messages;

    public ClanCommand(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        this.messages = lateTimeClan.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clan")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;



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

            var clan = new ClanModel(
                    clanName,
                    clanTag,
                    player.getUniqueId().toString(),
                    "open",
                    10,
                    Map.of(
                            player.getUniqueId(), ClanGroupModel.OWNER,
                            UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), ClanGroupModel.MANAGER,
                            UUID.fromString("7dcb46db-d22c-4157-8df9-aa88587ffd17"), ClanGroupModel.MODERATOR,
                            UUID.fromString("36eacca7-ac75-4115-8390-97affd4d77fd"), ClanGroupModel.MEMBER
                    )
            );

            this.lateTimeClan.getClanDatabase().createClan(clan);

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.2f, 1.0f);
            player.sendMessage(this.messages.compileMessage(
                    "Du hast den Clan %s erfolgreich erstellt!",
                    clanTag
            ));

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("info")) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            player.sendMessage(this.messages.compileMessage(
                    "Spielerdaten werden abgerufen...\n "
            ));

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var memberList = ClanDecoder.getPlayersFromClan(clan);

            // TODO: Remove comment
            //if (memberList.size() <= 1) {
            //    player.sendMessage(this.messages.compileMessage(
            //            "Dein Clan hat derzeit keine Mitglieder!"
            //    ));
            //    return true;
            //}

            player.sendMessage(this.messages.compileMessage(
                    "Folgende Mitglieder sind derzeit in deinem Clan:"
            ));

            Comparator<ClanPlayer> comparator = Comparator.comparingInt(clanPlayer -> clanPlayer.getClanGroup().getPermissionLevel());
            memberList.sort(comparator.reversed());

            memberList.forEach(clanPlayer -> player.sendMessage(this.messages.compileMessage(
                    " " + clanPlayer.getName() +
                            " §8| (" + clanPlayer.getClanGroup().getColor() + clanPlayer.getClanGroup().getName() + "§8)"
                    )
            ));
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("open")) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            player.sendMessage(this.messages.compileMessage(
                    "Spielerdaten werden abgerufen...\n "
            ));

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            player.openInventory(new ClanMemberPage(this.lateTimeClan, clan).getInventory());

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("open2")) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            player.sendMessage(this.messages.compileMessage(
                    "Spielerdaten werden abgerufen...\n "
            ));

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            player.openInventory(new ClanInfoPage(this.lateTimeClan, clan).getInventory());

        }

        if (args.length == 1 && args[0].equalsIgnoreCase("stats")) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());

            player.sendMessage(this.messages.compileMessage(
                    "Statistiken werden abgerufen...\n "
            ));

            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var memberList = ClanDecoder.getPlayersFromClan(clan);

            // TODO: Remove comment
            //if (memberList.size() <= 1) {
            //    player.sendMessage(this.messages.compileMessage(
            //            "Dein Clan hat derzeit keine Mitglieder!"
            //    ));
            //    return true;
            //}

            player.sendMessage(this.messages.compileMessage(
                    "Clan-Statistiken:"
            ));

            player.sendMessage(this.messages.compileMessage(
                    "Name: %s",
                    clan.getName()
            ));

            player.sendMessage(this.messages.compileMessage(
                    "Tag: %s",
                    clan.getTag()
            ));

            player.sendMessage(this.messages.compileMessage(
                    "Mitgliederanzahl: %s",
                    "" + clan.getMembers().size()
            ));


        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        ArrayList<String> matches = new ArrayList<>();

        if (args.length == 1) {
            matches.add("create");
            matches.add("info");
            matches.add("open");
            matches.add("open2");
            matches.add("stats");
        }

        return matches;
    }
}

