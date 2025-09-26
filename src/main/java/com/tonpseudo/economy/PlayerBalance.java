package com.tonpseudo.economy;

import java.util.EnumMap;

public class PlayerBalance {
    private final EnumMap<Currency, Long> map = new EnumMap<>(Currency.class);

    public PlayerBalance() {
        for (Currency c : Currency.values()) {
            map.put(c, 0L);
        }
    }

    public long get(Currency c) {
        return map.getOrDefault(c, 0L);
    }

    public void set(Currency c, long amount) {
        map.put(c, amount);
    }

    public void add(Currency c, long amount) {
        long old = get(c);
        map.put(c, old + amount);
    }

    public boolean take(Currency c, long amount) {
        long old = get(c);
        if (old < amount) {
            return false;
        }
        map.put(c, old - amount);
        return true;
    }
}
