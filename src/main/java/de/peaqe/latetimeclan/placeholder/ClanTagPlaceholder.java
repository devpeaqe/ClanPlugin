package de.peaqe.latetimeclan.placeholder;

import de.peaqe.latetimeclan.objects.ClanPlayerObject;
import de.peaqe.latetimeclan.util.color.Hex;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 23.03.2024 | 12:00 Uhr
 * *
 */

public class ClanTagPlaceholder extends PlaceholderExpansion {

    public ClanTagPlaceholder() {
        this.register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clan";
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
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {

        if (params.equalsIgnoreCase("tag")) {
            var clanPlayer = ClanPlayerObject.fromPlayer(player);
            if (clanPlayer == null) return "";
            if (clanPlayer.getClan() == null) return "";

            return "§8[" + Hex.color(clanPlayer.getClan().getColor()) +
                    clanPlayer.getClan().getTag().toUpperCase() + "§8]";
        }

        return "";
    }

}
