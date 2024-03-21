package de.peaqe.latetimeclan.objects;

import java.util.List;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 08:25 Uhr
 * *
 */

public enum ClanGroup {

    MEMBER(0, "Mitglied", "§7"),
    MODERATOR(1, "Moderator", "§3"),
    MANAGER(2, "Manager", "§c"),
    OWNER(3, "Inhaber", "§4");

    private final int permissionLevel;
    private final String name;
    private final String color;

    ClanGroup(int permissionLevel, String name, String color) {
        this.permissionLevel = permissionLevel;
        this.name = name;
        this.color = color;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public static List<ClanGroup> getGroups() {
        return List.of(MEMBER, MODERATOR, MANAGER, OWNER);
    }

}
