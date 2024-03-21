package de.peaqe.latetimeclan.provider.cache;

import de.peaqe.latetimeclan.objects.ClanObject;
import de.peaqe.latetimeclan.objects.util.ClanDecoder;

import java.util.HashMap;
import java.util.Map;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 20.02.2024 | 12:30 Uhr
 * *
 */

public class SimpleCache {

    private final Map<String, String> cache;

    public SimpleCache() {
        this.cache = new HashMap<>();
    }

    public void cache(ClanObject clanObject) {
        this.cache.put(clanObject.getTag(), ClanDecoder.toString(clanObject));
    }

    public ClanObject get(String clanTag) {
        return ClanDecoder.getClanModel(this.cache.get(clanTag));
    }

    public void remove(String clanTag) {
        this.cache.remove(clanTag);
    }

    public void clear() {
        this.cache.clear();
    }

    public int size() {
        return this.cache.size();
    }

    public boolean containsKey(String clanTag) {
        return this.cache.containsKey(clanTag);
    }

    public boolean containsValue(ClanObject clanObject) {
        return this.cache.containsValue(ClanDecoder.toString(clanObject));
    }

    public Map<String, String> getCache() {
        return this.cache;
    }

}
