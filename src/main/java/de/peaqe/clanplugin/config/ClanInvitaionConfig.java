package de.peaqe.clanplugin.config;

import de.peaqe.clanplugin.ClanPlugin;
import de.peaqe.clanplugin.objects.ClanObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 12:48 Uhr
 * *
 */

public class ClanInvitaionConfig {

    private final File file;
    private final FileConfiguration config;

    public ClanInvitaionConfig(ClanPlugin clanPlugin) {

        this.file = new File(clanPlugin.getDataFolder().getAbsolutePath(), "invitations.yml");

        if (!file.exists()) {
            try {

                var bool = clanPlugin.getDataFolder().mkdirs();
                var bool1 = file.createNewFile();

                if (bool) {
                    Bukkit.getLogger().log(Level.INFO,
                            "Created Folder: " + clanPlugin.getDataFolder().getAbsolutePath());
                }

                if (bool1) {
                    Bukkit.getLogger().log(Level.INFO,
                            "Created Folder: " + clanPlugin.getDataFolder().getAbsolutePath());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    private final String clanInvitationPath = "Inviations.";

    public boolean addInvitation(ClanObject clanObject, UUID targetUUID) {

        var invitationList = this.config.getStringList(clanObject.getTag());

        invitationList.add(targetUUID.toString());
        this.config.set(clanInvitationPath + clanObject.getTag(), invitationList);

        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public void removeInvitation(ClanObject clanObject, UUID targetUUID) {

        var invitationList = this.config.getStringList(clanObject.getTag());

        invitationList.remove(targetUUID.toString());
        this.config.set(clanInvitationPath + clanObject.getTag(), invitationList);

        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearInvitations(ClanObject clanObject) {
        this.config.set(clanInvitationPath + clanObject.getTag(), null);
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getClansWithInvitation(UUID targetUUID) {
        var clansWithInvitation = new ArrayList<String>();

        for (var clanTag : this.config.getKeys(false)) {
            var invitationList = this.config.getStringList(this.clanInvitationPath + clanTag);
            if (invitationList.contains(targetUUID.toString())) {
                clansWithInvitation.add(clanTag);
            }
        }
        return clansWithInvitation;
    }

    public boolean wasInvited(ClanObject clanObject, UUID uuid) {
        var invitationList = this.config.getStringList(this.clanInvitationPath + clanObject.getTag());
        return invitationList.contains(uuid.toString());
    }
}
