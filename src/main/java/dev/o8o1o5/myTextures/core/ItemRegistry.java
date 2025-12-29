package dev.o8o1o5.myTextures.core;

import dev.o8o1o5.myTextures.MyTextures;
import dev.o8o1o5.myTextures.api.TexturesItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemRegistry {
    private final MyTextures plugin;
    private final NamespacedKey itemIdKey;
    // 모든 데이터를 ItemBuilder 기반으로 관리
    private final Map<String, TexturesItemBuilder> itemList = new HashMap<>();

    public ItemRegistry(MyTextures plugin) {
        this.plugin = plugin;
        this.itemIdKey = new NamespacedKey(plugin, "item_id");
    }

    /**
     * 데이터를 파일(config.yml)에 저장
     */
    public void saveItems() {
        FileConfiguration config = plugin.getConfig();
        config.set("items", null); // 기존 데이터 초기화

        for (Map.Entry<String, TexturesItemBuilder> entry : itemList.entrySet()) {
            String id = entry.getKey();
            TexturesItemBuilder builder = entry.getValue();

            String path = "items." + id + ".";
            config.set(path + "material", builder.getMaterial().name());
            config.set(path + "display_name", builder.getDisplayName());
            config.set(path + "lore", builder.getLore());

            // NBT 데이터 저장 (선택 사항: 문자열 맵으로 변환하여 저장 가능)
        }
        plugin.saveConfig();
        plugin.getLogger().info("모든 아이템 데이터가 빌더 기준으로 저장되었습니다.");
    }

    /**
     * 파일에서 데이터를 불러와 빌더 객체로 변환
     */
    public void loadItems() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("items");

        if (section == null) return;

        itemList.clear();
        for (String id : section.getKeys(false)) {
            String path = "items." + id + ".";

            Material material = Material.getMaterial(config.getString(path + "material", "PAPER"));
            String displayName = config.getString(path + "display_name");
            List<String> lore = config.getStringList(path + "lore");

            TexturesItemBuilder builder = new TexturesItemBuilder(id)
                    .material(material)
                    .name(displayName)
                    .lore(lore);

            itemList.put(id, builder);
        }
        plugin.getLogger().info(itemList.size() + "개의 아이템 빌더를 로드했습니다.");
    }

    /**
     * 외부 API 및 내부 커맨드에서 호출하는 등록 메서드
     */
    public void register(TexturesItemBuilder builder) {
        itemList.put(builder.getId(), builder);
        // 등록 시 리소스 파일(json 등) 자동 생성 로직 연동
        plugin.getFileManager().generateResourceFiles(builder.getId());
    }

    /**
     * 아이템 생성 실체화
     */
    public ItemStack createItem(String id) {
        TexturesItemBuilder builder = itemList.get(id);
        if (builder == null) return null;

        ItemStack item = new ItemStack(builder.getMaterial());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // 1. 핵심: 문자열 ID를 모델로 연결
            meta.getPersistentDataContainer().set(itemIdKey, PersistentDataType.STRING, id);

            meta.setItemModel(new NamespacedKey("mytextures", id));

            // 2. 이름 설정 (Adventure Component & 이탈릭 제거)
            if (builder.getDisplayName() != null) {
                meta.displayName(Component.text()
                        .content(builder.getDisplayName())
                        .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                        .build());
            }

            // 3. 로어 설정
            if (!builder.getLore().isEmpty()) {
                List<Component> loreComponents = builder.getLore().stream()
                        .map(line -> Component.text(line).decoration(TextDecoration.ITALIC, false))
                        .collect(Collectors.toList());
                meta.lore(loreComponents);
            }

            // 4. 커스텀 NBT 적용 (MyEconomy 등에서 보낸 데이터)
            builder.getPersistentData().forEach((key, value) ->
                    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value));

            item.setItemMeta(meta);
        }
        return item;
    }

    public String getIdFromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(itemIdKey, PersistentDataType.STRING);
    }

    public Map<String, TexturesItemBuilder> getItemList() {
        return itemList;
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
        loadItems();

        int count = 0;
        for (String id : itemList.keySet()) {
            if (plugin.getFileManager().generateResourceFiles(id)) {
                count++;
            }
        }
        plugin.getLogger().info("플러그인 재로드 완료: " + count + "개의 리소스 파일 갱신.");
    }

    // 기존 호환성을 위한 체크 메서드
    public boolean exists(String id) {
        return itemList.containsKey(id);
    }
}