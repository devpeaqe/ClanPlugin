package de.peaqe.clanplugin.objects.util;

import de.peaqe.clanplugin.objects.ClanGroup;
import de.peaqe.clanplugin.objects.ClanObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:25 Uhr
 * *
 */

public class ClanDecoder {

    public static String mapToString(Map<UUID, ClanGroup> map) {

        var stringBuilder = new StringBuilder();

        for (Map.Entry<UUID, ClanGroup> entry : map.entrySet()) {
            stringBuilder.append(entry.getValue().name()).append("=").append(entry.getKey()).append(",");
        }

        if (!stringBuilder.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static Map<UUID, ClanGroup> stringToMap(String str) {

        Map<UUID, ClanGroup> map = new HashMap<>();

        if (str != null && !str.isEmpty()) {
            String[] pairs = str.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    ClanGroup value = ClanGroup.valueOf(keyValue[0]);
                    UUID key = UUID.fromString(keyValue[1]);
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    public static String toString(ClanObject clan) {
        return "Name: " + clan.getName() + "\n" +
                "Tag: " + clan.getTag() + "\n" +
                "Clan Founder UUID: " + clan.getClanFounderUUID() + "\n" +
                "Clan Invitation Status: " + clan.getClanInvitationStatus().getStatus() + "\n" +
                "Max Size: " + clan.getMaxSize() + "\n" +
                "Members: " + mapToString(clan.getMembers()) + "\n";
    }

}
