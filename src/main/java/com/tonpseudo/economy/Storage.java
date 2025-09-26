package com.tonpseudo.economy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Storage {
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<UUID, PlayerBalance>>() {}.getType();

    private Map<UUID, PlayerBalance> data = new HashMap<>();
    private File file;

    public Storage() {
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        this.file = new File(configDir, "economymod_balances.json");
        load();
    }

    public PlayerBalance get(UUID player) {
        return data.computeIfAbsent(player, k -> new PlayerBalance());
    }

    public void save() {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, MAP_TYPE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (!file.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            Map<UUID, PlayerBalance> loaded = GSON.fromJson(reader, MAP_TYPE);
            if (loaded != null) {
                data = loaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
