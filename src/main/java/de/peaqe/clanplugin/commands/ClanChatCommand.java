package de.peaqe.clanplugin.commands;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.messages.Messages;
import de.peaqe.clanplugin.objects.ClanPlayerObject;
import de.peaqe.clanplugin.objects.util.ClanAction;
import de.peaqe.clanplugin.webhook.DiscordWebhook;
import org.bukkit.Bukkit;
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

    private final ClanPlugin clanPlugin;
    private final Messages messages;

    public ClanChatCommand(ClanPlugin clanPlugin) {
        this.clanPlugin = clanPlugin;
        this.messages = clanPlugin.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("clanchat")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clanchat")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) return true;

        if (args.length != 0) {

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

            if (!clanPlayer.hasPermission(ClanAction.CHAT)) {
                player.sendMessage(this.messages.compileMessage(
                        "Du bist derzeit nicht berechtigt im %s zu schreiben!",
                        "Clan-Chat"
                ));
                return true;
            }

            if (!clanPlayer.getClan().getSettings().isClanChatToggled()) {
                player.sendMessage(this.messages.compileMessage(
                        "Der Clan-Chat ist derzeit %s!", "§cdeaktiviert"
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
                    "§8» §9ClanChat §8┃ §7" +
                    clanPlayer.getClanGroup().getColor() +
                    //clanPlayer.getClanGroup().getName() + " §8• " +
                    clanPlayer.getName() + " §8» §7" +
                    stringBuilder.toString().trim()
            );

            this.clanPlugin.getWebhookSender().sendWebhook(
                    new DiscordWebhook.EmbedObject().setTitle("Clan Nachricht")
                            .addField("Mitglied", player.getName(), true)
                            .addField("Nachricht", stringBuilder.toString().trim(), true)
                            .addField("Clan", clanPlayer.getClan().getName(), true)
                            .addField("Clan-Tag", clanPlayer.getClan().getTag(), true)
                            .setFooter("× NoviaMC.DE » Clan-System", null)
                            .setColor(Color.GREEN)
            );

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}

