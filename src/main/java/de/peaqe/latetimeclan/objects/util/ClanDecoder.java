package de.peaqe.latetimeclan.objects.util;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanGroup;
import de.peaqe.latetimeclan.objects.ClanInvitationStatus;
import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.provider.util.ClanProperty;

import java.sql.SQLException;
import java.util.*;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:25 Uhr
 * *
 */

public class ClanDecoder {

    public static ClanObject getClanModel(String clanTag) {
        return LateTimeClan.getInstance().getClanDatabase().getClan(clanTag);
    }

    public static ClanObject getClanModel(UUID clanFounderUUID) {
        try {
            return LateTimeClan.getInstance().getClanDatabase()
                    .getClanModelByCondition(ClanProperty.CLAN_FOUNDER_UUID, clanFounderUUID.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public static ClanObject fromString(String str) {

        var parts = str.split("\n");
        var clan = new ClanObject();

        for (String part : parts) {

            var keyValue = part.split(": ");

            if (keyValue.length == 2) {

                var key = keyValue[0].trim();
                var value = keyValue[1].trim();

                switch (key) {
                    case "Name":
                        clan.setName(value);
                        break;
                    case "Tag":
                        clan.setTag(value);
                        break;
                    case "Clan Founder UUID":
                        clan.setClanFounderUUID(value);
                        break;
                    case "Clan Invitation Status":
                        clan.setClanInvitationStatus(ClanInvitationStatus.getFromStatus(value));
                        break;
                    case "Max Size":
                        clan.setMaxSize(Integer.parseInt(value));
                        break;
                    case "Members":
                        clan.setMembers(stringToMap(value));
                        break;
                    default:
                        break;
                }
            }
        }
        return clan;
    }

    public List<ClanPlayerObject> getPlayersFromClan(String clanTag) {

        var clan = getClanModel(clanTag);
        if (clan == null) return new ArrayList<>();

        var members = getClanModel(clanTag).getMembers();
        var memberList = new ArrayList<ClanPlayerObject>();

        members.forEach((uuid, clanGroupModel) -> memberList.add(ClanPlayerObject.fromPlayer(uuid)));
        return memberList;

    }

    public static List<ClanPlayerObject> getPlayersFromClan(ClanObject clan) {

        var members = getClanModel(clan.getTag()).getMembers();
        var memberList = new ArrayList<ClanPlayerObject>();

        members.forEach((uuid, clanGroupModel) -> memberList.add(ClanPlayerObject.fromPlayer(uuid)));
        return memberList;
    }

}
