package de.peaqe.latetimeclan.commands;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 13.03.2024 | 19:50 Uhr
 * *
 */

public class ClanChatCommand implements CommandExecutor, TabExecutor {

    private final LateTimeClan lateTimeClan;
    private final Messages messages;

    public ClanChatCommand(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        this.messages = lateTimeClan.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("clanchat")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clanchat")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length != 0) {

            var clan = this.lateTimeClan.getClanDatabase().getClanModelOfMember(player.getUniqueId());
            if (clan == null) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit in keinem Clan!"
                ));
                return true;
            }

            var clanPlayer = ClanPlayer.fromPlayer(player);
            if (clanPlayer == null) {
                player.sendMessage(this.messages.compileMessage(
                        "§cEs ist ein Fehler aufgetreten! Bitte wende dich an einen Administrator."
                ));
                return true;
            }

            if (!clanPlayer.hasPermission(ClanAction.CHAT)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit nicht berechtigt im %s zu schreiben!",
                        "Clan-Chat"
                ));
                return true;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (var arg : args) {
                stringBuilder
                        .append(arg.replace('&', '§').replace("%s", ""))
                        .append(" ");
            }

            clanPlayer.sendMessage(
                    this.messages.sendClanMessage(
                            clanPlayer, stringBuilder.toString().trim()
                    )
            );

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}

