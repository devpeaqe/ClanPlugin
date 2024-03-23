package de.peaqe.latetimeclan.util;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;
import de.peaqe.latetimeclan.util.heads.Head;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 12.03.2024 | 11:25 Uhr
 * *
 */

public class ClanUtil {

    public static ClanInvitationStatus getClanInvitationStatus(ClanObject clanObject) {
        if (clanObject.getMembers().size() >= clanObject.getMaxSize()) return ClanInvitationStatus.CLOSED;
        return clanObject.getClanInvitationStatus();
    }

    public static boolean isPermitted(ClanPlayerObject sender, ClanPlayerObject target, ClanAction clanAction) {
        return (sender.hasPermission(clanAction) &&
                sender.getClanGroup().getPermissionLevel() > target.getClanGroup().getPermissionLevel());

    }

    public static String getPlayerHeadUrl(String playerName) {
        try {

            var url = "https://crafatar.com/renders/head/" + playerName + "?overlay";
            var connection = (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                var scanner = new Scanner(connection.getInputStream());
                var response = new StringBuilder();

                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }

                scanner.close();
                return response.toString();
            } else {
                throw new IOException("Failed to fetch player head, response code: " + responseCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String compressInt(int number) {
        var formatter = new DecimalFormat("#,###");
        return "§b" + formatter.format(number).replace(",", "§7.§b");
    }

    public static String compressIntWithoutColor(int number) {
        var formatter = new DecimalFormat("#,###");
        return formatter.format(number).replace(",", ".");
    }

    public static int decompressString(String compressedNumber) {
        try {
            return Integer.parseInt(compressedNumber.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static ItemStack getGoBackItem() {
        return new ItemBuilder(Head.compile(Head.GO_BACK))
                .setDisplayName("§8• §cZurück")
                .glow()
                .build();
    }

    public static String formatDate(Date date, String color) {
        var format = new SimpleDateFormat("dd.MM.yyyy | HH:mm 'Uhr'");
        format.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        return color + format.format(date)
                .replace("|", "§7|" + color)
                .replace(".", "§7." + color)
                .replace(":", "§7:" + color);
    }

    @Nullable
    public static String isBlockedChar(String name) {

        var blockedChars = LateTimeClan.getInstance().getBlockedWordsConfig().getBlockedWords();
        var atomicBlockedChar = new AtomicReference<String>();

        blockedChars.forEach(object -> {
            if (!object.toLowerCase().contains(name.toLowerCase())) return;
            atomicBlockedChar.set(name);
        });

        if (atomicBlockedChar.get() == null || atomicBlockedChar.get().isEmpty()) return null;
        return atomicBlockedChar.get();
    }

    @Nullable
    public static String getCheckedWort(String name) {

        var blockedChars = LateTimeClan.getInstance().getBlockedWordsConfig().getBlockedWords();
        var atomicBlockedChar = new AtomicReference<String>();

        blockedChars.forEach(object -> {
            if (!object.toLowerCase().contains(name.toLowerCase())) return;
            atomicBlockedChar.set(object);
        });

        if (atomicBlockedChar.get() == null || atomicBlockedChar.get().isEmpty()) return null;
        return atomicBlockedChar.get();
    }

}
