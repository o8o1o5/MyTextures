package dev.o8o1o5.myTextures.manager;

import dev.o8o1o5.myTextures.CustomItem;
import dev.o8o1o5.myTextures.MyTextures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    private final MyTextures plugin;
    private final Map<String, CustomItem> itemList = new HashMap<>();

    public ItemRegistry(MyTextures plugin) {
        this.plugin = plugin;
    }

    public void saveItems() {
        FileConfiguration config = plugin.getConfig();
        config.set("items", null);

        for (Map.Entry<String, CustomItem> entry : itemList.entrySet()) {
            String id = entry.getKey();
            CustomItem item = entry.getValue();

            config.set("items." + id + ".material", item.getBaseItem().name());
            config.set("items." + id + ".display_name", item.getDisplayName());
        }
        plugin.saveConfig();
        plugin.getLogger().info("모든 아이템 데이터가 저장되었습니다.");
    }

    public void loadItems() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("items");

        if (section == null) return;

        itemList.clear();
        for (String id : section.getKeys(false)) {
            String matName = config.getString("items." + id + ".material");
            String displayName = config.getString("items." + id + ".display_name");
            Material material = Material.getMaterial(matName != null ? matName : "PAPER");

            CustomItem item = new CustomItem(id, material);
            if (displayName != null) item.setDisplayName(displayName);

            itemList.put(id, item);
        }
        plugin.getLogger().info(itemList.size() + "개의 아이템 데이터를 불러왔습니다.");
    }

    public void registerItemData(String id, Material material) {
        itemList.put(id, new CustomItem(id, material));
    }

    public boolean updateDisplayName(String id, String name) {
        CustomItem item = itemList.get(id);
        if (item == null) return false;
        item.setDisplayName(name);
        return true;
    }

    public ItemStack createItem(String id) {
        CustomItem data = itemList.get(id);
        if (data == null) return null;

        ItemStack item = new ItemStack(data.getBaseItem());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setItemModel(new NamespacedKey("mytextures", id));

            Component nameComponent = Component.text()
                    .content(data.getDisplayName())
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .build();

            meta.displayName(nameComponent);

            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean removeItem(String id) {
        if (!itemList.containsKey(id)) return false;

        itemList.remove(id);
        saveItems();

        plugin.getFileManager().deleteResourceFiles(id);

        return true;
    }

    public void reload() {
        plugin.reloadConfig();

        itemList.clear();
        loadItems();

        int count = 0;
        for (String id : itemList.keySet()) {
            if (plugin.getFileManager().generateResourceFiles(id)) {
                count ++;
            }
        }

        plugin.getLogger().info("플러그인 설정 및 " + count + "개의 리소스 파일이 재로드되었습니다.");
    }

    public Map<String, CustomItem> getItemList() {
        return itemList;
    }

    public boolean exists(String id) {
        return itemList.containsKey(id);
    }
}
