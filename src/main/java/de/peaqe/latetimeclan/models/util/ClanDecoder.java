package de.peaqe.latetimeclan.models.util;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.models.ClanGroupModel;
import de.peaqe.latetimeclan.models.ClanModel;
import de.peaqe.latetimeclan.provider.util.Property;

import javax.annotation.Nullable;
import java.sql.SQLException;
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

    @Nullable
    public static ClanModel getClanModel(String clanTag) {
        return LateTimeClan.getInstance().getClanDatabase().getClan(clanTag);
    }

    public static ClanModel getClanModel(UUID clanFounderUUID) {
        try {
            return LateTimeClan.getInstance().getClanDatabase()
                    .getClanModelByCondition(Property.CLAN_FOUNDER_UUID, clanFounderUUID.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String mapToString(Map<ClanGroupModel, UUID> map) {

        var stringBuilder = new StringBuilder();

        for (Map.Entry<ClanGroupModel, UUID> entry : map.entrySet()) {
            stringBuilder.append(entry.getKey().name()).append("=").append(entry.getValue()).append(",");
        }

        if (!stringBuilder.isEmpty()) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static Map<ClanGroupModel, UUID> stringToMap(String str) {

        Map<ClanGroupModel, UUID> map = new HashMap<>();

        if (str != null && !str.isEmpty()) {
            String[] pairs = str.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    ClanGroupModel key = ClanGroupModel.valueOf(keyValue[0]);
                    UUID value = UUID.fromString(keyValue[1]);
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    public static String toString(ClanModel clan) {
        return "Name: " + clan.getName() + "\n" +
                "Tag: " + clan.getTag() + "\n" +
                "Clan Founder UUID: " + clan.getClanFounderUUID() + "\n" +
                "Clan Invitation Status: " + clan.getClanInvitationStatus() + "\n" +
                "Max Size: " + clan.getMaxSize() + "\n" +
                "Members: " + mapToString(clan.getMembers()) + "\n";
    }

    public static ClanModel fromString(String str) {

        var parts = str.split("\n");
        var clan = new ClanModel();

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
                        clan.setClanInvitationStatus(value);
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

}
