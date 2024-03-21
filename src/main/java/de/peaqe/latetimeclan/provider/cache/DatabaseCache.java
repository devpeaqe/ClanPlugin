package de.peaqe.latetimeclan.provider.cache;

import de.peaqe.latetimeclan.objects.ClanObject;

import java.util.HashMap;
import java.util.Map;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 18.02.2024 | 14:03 Uhr
 * *
 */

public class DatabaseCache {

    private final Map<String, ClanObject> clanCache;

    public DatabaseCache() {
        this.clanCache = new HashMap<>();
    }

    public void addEntry(String key, ClanObject clanObject) {
        this.clanCache.put(key, clanObject);
    }

    public ClanObject getEntry(String key) {
        return this.clanCache.get(key);
    }

    public void removeEntry(String key) {
        this.clanCache.remove(key);
    }

    public void clear() {
        this.clanCache.clear();
    }

    public int size() {
        return this.clanCache.size();
    }

    public boolean containsKey(String key) {
        return this.clanCache.containsKey(key);
    }

    public boolean containsValue(ClanObject clanObject) {
        return this.clanCache.containsValue(clanObject);
    }

    public Map<String, ClanObject> getClanCache() {
        return this.clanCache;
    }

}
