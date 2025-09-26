package com.tonpseudo.economy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {
    private static final Gson GSON = new Gson();
    private static final File SAVE_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "balances.json");
    private static final Type TYPE = new TypeToken<Map<UUID, PlayerBalance>>() {}.getType();

    private static EconomyManager INSTANCE = null;

    private final Map<UUID, PlayerBalance> balances = new ConcurrentHashMap<>();

    private EconomyManager() {
        loadAll();
    }

    public static EconomyManager get() {
        if (INSTANCE == null) {
            INSTANCE = new EconomyManager();
        }
        return INSTANCE;
    }

    public long getBalance(UUID player, Currency c) {
        return balances.computeIfAbsent(player, k -> new PlayerBalance()).get(c);
    }

    public void setBalance(UUID player, Currency c, long amount) {
        balances.computeIfAbsent(player, k -> new PlayerBalance()).set(c, amount);
    }

    public void addBalance(UUID player, Currency c, long amount) {
        balances.computeIfAbsent(player, k -> new PlayerBalance()).add(c, amount);
    }

    public boolean takeBalance(UUID player, Currency c, long amount) {
        return balances.computeIfAbsent(player, k -> new PlayerBalance()).take(c, amount);
    }

    public void saveAll() {
        try (FileWriter writer = new FileWriter(SAVE_FILE)) {
            GSON.toJson(balances, TYPE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAll() {
        if (!SAVE_FILE.exists()) return;
        try (FileReader reader = new FileReader(SAVE_FILE)) {
            Map<UUID, PlayerBalance> loaded = GSON.fromJson(reader, TYPE);
            if (loaded != null) balances.putAll(loaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
