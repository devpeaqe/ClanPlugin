package de.peaqe.clanplugin.util.database;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 17.03.2024 | 13:34 Uhr
 * *
 */

public record DatabaseConnection(String hostname, String username, String password, String database, int port) {

}
