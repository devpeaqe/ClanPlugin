package de.peaqe.latetimeclan.test;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.messages.Messages;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 19.02.2024 | 22:34 Uhr
 * *
 */

public class TestCommand implements CommandExecutor, TabExecutor {

    private final LateTimeClan lateTimeClan;
    private final Messages messages;
    public TestCommand(LateTimeClan lateTimeClan) {
        this.lateTimeClan = lateTimeClan;
        this.messages = lateTimeClan.getMessages();
        Objects.requireNonNull(Bukkit.getPluginCommand("test")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("test")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {

        // Console test command
        var randomFounder = UUID.randomUUID();

        // Create clan
        this.lateTimeClan.getClanDatabase().createClan(
                new ClanModel(
                        "Test123",
                        "Test",
                        randomFounder.toString(),
                        "open",
                        10,
                        Map.of(
                                ClanGroupModel.OWNER, randomFounder
                        )
                )
        );

        // Load clan data
        var clan = this.lateTimeClan.getClanDatabase().getClan("test");

        if (clan == null) {
            System.out.println(this.messages.compileMessage(
                    "Der Clan %s konnte nicht gefunden werden!",
                    "test"
            ));
            return true;
        }

        System.out.println(this.messages.compileMessage(
                "Der Clan %s hat folgende Mitglieder: %s",
                clan.getTag(),
                clan.getMembers().toString()
        ));

        //System.out.println("Cache: " + this.lateTimeClan.getDatabaseCache().getClanCache().toString());

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, @NotNull String[] args) {



        return null;
    }
}
