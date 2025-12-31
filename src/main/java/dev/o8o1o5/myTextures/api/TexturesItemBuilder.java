package dev.o8o1o5.myTextures.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TexturesItemBuilder {
    private final String id;
    private Material material = Material.PAPER;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private final Map<NamespacedKey, String> persistentData = new HashMap<>();
    private boolean shining;

    public TexturesItemBuilder(String id) {
        this.id = id;
    }

    public TexturesItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public TexturesItemBuilder name(String name) {
        this.displayName = name;
        return this;
    }

    public TexturesItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public TexturesItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public TexturesItemBuilder nbt(NamespacedKey key, String value) {
        this.persistentData.put(key, value);
        return this;
    }

    public TexturesItemBuilder shining(Boolean shining) {
        this.shining = shining;
        return this;
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public Map<NamespacedKey, String> getPersistentData() { return persistentData; }

    public boolean isShining() { return shining; }
}
