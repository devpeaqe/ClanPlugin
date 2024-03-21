package de.peaqe.latetimeclan.util;

import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.objects.util.ClanAction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

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

}
