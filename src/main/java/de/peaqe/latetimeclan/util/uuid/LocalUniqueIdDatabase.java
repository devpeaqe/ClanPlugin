package de.peaqe.latetimeclan.util.uuid;

import java.util.Map;
import java.util.UUID;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 01.03.2024 | 09:42 Uhr
 * *
 */

public class LocalUniqueIdDatabase {
    public static Map<UUID, String> getLocalUniqueIdDatabaseUniqueId() {
        return Map.of(
                UUID.fromString("c2c91bd4-e526-45cb-8062-a81daf53ffc6"), "NQRMAN".toLowerCase(),
                UUID.fromString("65239039-56a8-453a-9ed8-80e43e5c4df6"), "veraxOG".toLowerCase(),
                UUID.fromString("a4cc39fe-d210-4e7a-ab15-1e84d8158190"), "Laurinllg".toLowerCase(),
                UUID.fromString("7468d900-cef5-4dba-98a6-43632d30b3d7"), "ExMicrosoftDev".toLowerCase()
        );
    }

    public static Map<String, UUID> getLocalUniqueIdDatabaseName() {
        return Map.of(
                "NQRMAN".toLowerCase(), UUID.fromString("c2c91bd4-e526-45cb-8062-a81daf53ffc6"),
                "veraxOG".toLowerCase(), UUID.fromString("65239039-56a8-453a-9ed8-80e43e5c4df6"),
                "Laurinllg".toLowerCase(), UUID.fromString("a4cc39fe-d210-4e7a-ab15-1e84d8158190"),
                "ExMicrosoftDev".toLowerCase(), UUID.fromString("7468d900-cef5-4dba-98a6-43632d30b3d7")
        );
    }
}
