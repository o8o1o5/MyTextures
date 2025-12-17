package dev.o8o1o5.myTextures.manager;

import dev.o8o1o5.myTextures.MyTextures;
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

public class ItemManager {

    private final MyTextures plugin;

    public  ItemManager(MyTextures plugin) {
        this.plugin = plugin;
    }

    /**
     * 1. 이미지를 복사합니다.
     * 2. 아이템 정의 JSON 을 생성합니다.
     * 3. 아이템 모델 JSON 을 생성합니다.
     */
    public boolean registerItem(String id) {
        File sourceImage = new File(plugin.getDataFolder(), "images/" + id + ".png");

        if (!sourceImage.exists()) {
            plugin.getLogger().warning(id + ".png 파일이 images 폴더에 존재하지 않습니다!");
            return false;
        }

        try {
            File destImage = new File(plugin.getDataFolder(), "export/assets/mytextures/textures/item/" + id + ".png");
            Files.copy(sourceImage.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);

            createItemDefinition(id);

            createItemModel(id);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createItemDefinition(String id) throws IOException {
        File file = new File(plugin.getDataFolder(), "export/assets/mytextures/items/" + id + ".json");
        String content = "{\n" +
                "   \"model\": {\n" +
                "       \"type\": \"minecraft:model\",\n" +
                "       \"model\": \"mytextures:item/custom/" + id + "\"\n" +
                "   }\n" +
                "}";
        writeFile(file, content);
    }

    private void createItemModel(String id) throws IOException {
        File file = new File(plugin.getDataFolder(), "export/assets/mytextures/models/item/custom/" + id +".json");
        String content = "{\n" +
                "   \"parent\": \"minecraft:item/generated\",\n" +
                "   \"textures\": {\n" +
                "       \"layer0\": \"mytextures:item/" + id + "\"\n" +
                "   }\n" +
                "}";
        writeFile(file, content);
    }

    public void createPackMeta() {
        File file = new File(plugin.getDataFolder(), "export/pack.mcmeta");

        String content = "{\n" +
                "   \"pack\": {\n" +
                "       \"description\": \"MyTextures Auto Generated Pack\",\n" +
                "       \"min_format\": 69,\n" +
                "       \"max_foramt\": 69\n" +
                "   }\n" +
                "}";

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            plugin.getLogger().info("최신 규격의 pack.mcmeta 를 성공적으로 생성했습니다.");
        } catch (IOException e) {
            plugin.getLogger().severe("pack.mcmeta 생성 중 오류 발생: " + e.getMessage());
        }
    }

    private void writeFile(File file, String content) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    public ItemStack createCustomItem(String id, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            NamespacedKey modelKey = new NamespacedKey("mytextures", id);
            meta.setItemModel(modelKey);

            NamespacedKey pdcKey = new NamespacedKey(plugin, "item_id");
            meta.getPersistentDataContainer().set(pdcKey, PersistentDataType.STRING, id);

            meta.setDisplayName(id);

            item.setItemMeta(meta);
        }
        return item;
    }
}
