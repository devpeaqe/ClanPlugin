package de.peaqe.latetimeclan.provider.cache;

import de.peaqe.latetimeclan.models.ClanModel;

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

    private final Map<String, ClanModel> clanCache;

    public DatabaseCache() {
        this.clanCache = new HashMap<>();
    }

    public void addEntry(String key, ClanModel clanModel) {
        this.clanCache.put(key, clanModel);
    }

    public ClanModel getEntry(String key) {
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

    public boolean containsValue(ClanModel clanModel) {
        return this.clanCache.containsValue(clanModel);
    }

    public Map<String, ClanModel> getClanCache() {
        return this.clanCache;
    }

}
