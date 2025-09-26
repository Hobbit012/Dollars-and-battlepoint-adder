package com.tonpseudo.economy;

import java.util.HashMap;
import java.util.Map;

public enum Currency {
    DOLLAR,
    BATTLEPOINT;

    private static final Map<String, Currency> ALIAS_MAP = new HashMap<>();

    static {
        // alias pour DOLLAR
        ALIAS_MAP.put("dollar", DOLLAR);
        ALIAS_MAP.put("dollars", DOLLAR);
        ALIAS_MAP.put("money", DOLLAR);
        ALIAS_MAP.put("usd", DOLLAR);
        ALIAS_MAP.put("pokédollar", DOLLAR);
        ALIAS_MAP.put("poke", DOLLAR);
        ALIAS_MAP.put("pokedollars", DOLLAR);

        // alias pour BATTLEPOINT
        ALIAS_MAP.put("bp", BATTLEPOINT);
        ALIAS_MAP.put("battlepoint", BATTLEPOINT);
        ALIAS_MAP.put("battlepoints", BATTLEPOINT);
        ALIAS_MAP.put("points", BATTLEPOINT);
        ALIAS_MAP.put("pt", BATTLEPOINT);
        ALIAS_MAP.put("bpoints", BATTLEPOINT);
    }

    public static Currency fromString(String input) {
        if (input == null) return null;
        return ALIAS_MAP.get(input.toLowerCase());
    }
}
