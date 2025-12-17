package dev.o8o1o5.myTextures;

import dev.o8o1o5.myTextures.command.TextureCommand;
import dev.o8o1o5.myTextures.manager.ItemManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MyTextures extends JavaPlugin {

    private CustomItem customItem;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MyTextures 플러그인이 활성화 되었습니다!");
        createPluginFolders();

        this.itemManager = new ItemManager(this);
        itemManager.createPackMeta();

        getCommand("mt").setExecutor(new TextureCommand(this));

        // 테스트용
        if (itemManager.registerItem("test_item")) {
            getLogger().info("테스트 아이템 등록 성공!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("MyTextures 플러그인이 비활성화 되었습니다.");
    }

    /**
     * 리소스팩 제작에 필요한 폴더 구조를 자동으로 생성합니다.
      */
    private void createPluginFolders() {
        String[] paths = {
                "images",                                       // 원본 이미지
                "export/assets/mytextures/items",               // 아이템 정의 JSON
                "export/assets/mytextures/models/item/custom",  // 모델 설정 JSON
                "export/assets/mytextures/textures/item"        // 리소스팩 이미지
        };

        for (String path : paths) {
            File folder = new File(getDataFolder(), path);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    getLogger().info("폴더 생성됨: " + path);
                }
            }
        }
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
