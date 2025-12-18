package dev.o8o1o5.myTextures.manager;

import dev.o8o1o5.myTextures.CustomItem;
import dev.o8o1o5.myTextures.MyTextures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
                    .build();

            meta.displayName(nameComponent);

            item.setItemMeta(meta);
        }
        return item;
    }

    public Map<String, CustomItem> getItemList() {
        return itemList;
    }

    public boolean exists(String id) {
        return itemList.containsKey(id);
    }
}
