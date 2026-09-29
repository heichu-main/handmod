package com.example.handmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HandConfig {
    // индексы параметров
    public static final int X = 0, Y = 1, Z = 2, RX = 3, RY = 4, RZ = 5, SCALE = 6;
    public static final String[] NAMES = {"Сдвиг X", "Сдвиг Y", "Сдвиг Z", "Поворот X", "Поворот Y", "Поворот Z", "Размер"};
    public static final float[] MIN = {-1f, -1f, -1f, -90f, -90f, -90f, 0.3f};
    public static final float[] MAX = { 1f,  1f,  1f,  90f,  90f,  90f, 2.0f};
    public static final float[] DEFAULT = {0, 0, 0, 0, 0, 0, 1f};

    public static class Hand {
        public float[] v = DEFAULT.clone();

        public void reset() { v = DEFAULT.clone(); }
    }

    public Hand main = new Hand();
    public Hand off = new Hand();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("handmod.json");
    private static HandConfig instance = new HandConfig();

    public static HandConfig get() { return instance; }

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                HandConfig c = GSON.fromJson(Files.readString(PATH), HandConfig.class);
                if (c != null && c.main != null && c.off != null
                        && c.main.v != null && c.main.v.length == DEFAULT.length
                        && c.off.v != null && c.off.v.length == DEFAULT.length) {
                    instance = c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Files.writeString(PATH, GSON.toJson(instance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
