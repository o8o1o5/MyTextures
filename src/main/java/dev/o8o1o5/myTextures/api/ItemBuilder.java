package dev.o8o1o5.myTextures.api;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private final String id;
    private Material material = Material.PAPER;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private final Map<NamespacedKey, String> persistentData = new HashMap<>();

    public ItemBuilder(String id) {
        this.id = id;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder name(String name) {
        this.displayName = name;
        return this;
    }

    public ItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder nbt(NamespacedKey key, String value) {
        this.persistentData.put(key, value);
        return this;
    }

    public String getId() { return id; }
    public Material getMaterial() { return material; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public Map<NamespacedKey, String> getPersistentData() { return persistentData; }
}
