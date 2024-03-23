package de.peaqe.latetimeclan.placeholder;

import de.peaqe.latetimeclan.LateTimeClan;
import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.03.2024 | 12:00 Uhr
 * *
 */

public class ClanTagPlaceholder extends PlaceholderExpansion {

    public ClanTagPlaceholder(LateTimeClan lateTimeClan) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ClanTagPlaceholder(lateTimeClan).register();
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clan_tag";
    }

    @Override
    public @NotNull String getAuthor() {
        return "peaqe";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {

        var uniqueId = offlinePlayer.getUniqueId();
        var player = Bukkit.getPlayer(uniqueId);

        if (player == null) return "";

        var clanPlayer = ClanPlayerObject.fromPlayer(player);
        if (clanPlayer == null) return "";

        return "§8[" + ChatColor.of("#" + clanPlayer.getClan().getColor()) +
                clanPlayer.getClan().getTag().toUpperCase() + "§8]";
    }

}
