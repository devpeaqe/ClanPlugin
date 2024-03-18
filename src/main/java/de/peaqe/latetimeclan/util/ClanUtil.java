package de.peaqe.latetimeclan.util;

import de.peaqe.latetimeclan.models.ClanInvitationStatus;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.models.ClanPlayer;
import de.peaqe.latetimeclan.models.util.ClanAction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static ClanInvitationStatus getClanInvitationStatus(ClanModel clanModel) {
        if (clanModel.getMembers().size() >= clanModel.getMaxSize()) return ClanInvitationStatus.CLOSED;
        return clanModel.getClanInvitationStatus();
    }

    public static boolean isPermitted(ClanPlayer sender, ClanPlayer target, ClanAction clanAction) {
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

}
