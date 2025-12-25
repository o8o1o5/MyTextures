package dev.o8o1o5.myTextures.api;

import dev.o8o1o5.myTextures.MyTextures;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class MyTexturesAPI {
    private static MyTextures plugin;
    private static NamespacedKey sourceKey;
    private static NamespacedKey idKey;

    public static void init(MyTextures instance) {
        plugin = instance;
        sourceKey = new NamespacedKey(instance, "source_plugin");
        idKey = new NamespacedKey(instance, "item_id");
    }

    // 출처를 기록하며 아이템을 생성하기 때문에, sourceApp 을 받아야 합니다.
    public static ItemStack createItem(String id, JavaPlugin sourceApp) {
        if (plugin == null) return null;

        ItemStack item = plugin.getItemRegistry().createItem(id);
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // 출신 플러그인 각인
            meta.getPersistentDataContainer().set(sourceKey, PersistentDataType.STRING, sourceApp.getName());
            // id 각인
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static String getSourcePluginName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return "UNKNOWN";
        return item.getItemMeta().getPersistentDataContainer()
                .getOrDefault(sourceKey, PersistentDataType.STRING, "UNKNOWN");
    }

    public static String getMyTexturesId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(idKey, PersistentDataType.STRING);
    }
}
