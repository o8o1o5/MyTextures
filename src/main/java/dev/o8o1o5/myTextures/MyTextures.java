package dev.o8o1o5.myTextures;

import dev.o8o1o5.myTextures.api.MyTexturesAPI;
import dev.o8o1o5.myTextures.command.TextureCommand;
import dev.o8o1o5.myTextures.manager.FileManager;
import dev.o8o1o5.myTextures.manager.ItemRegistry;
import dev.o8o1o5.myTextures.manager.WebServerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MyTextures extends JavaPlugin {

    private CustomItem customItem;
    private ItemRegistry itemRegistry;
    private FileManager fileManager;
    private WebServerManager webServerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.fileManager = new FileManager(this);
        this.itemRegistry = new ItemRegistry(this);
        this.webServerManager = new WebServerManager(this);

        MyTexturesAPI.init(this);

        saveDefaultConfig();

        itemRegistry.loadItems();

        fileManager.setupFolders();
        fileManager.createPackMeta();

        int port = getConfig().getInt("web-server.port", 8080);
        webServerManager.startserver(port);

        TextureCommand cmd = new TextureCommand(this);
        if (getCommand("mt") != null) {
            getCommand("mt").setExecutor(cmd);
            getCommand("mt").setTabCompleter(cmd);
        }

        getLogger().info("MyTextures 플러그인이 활성화 되었습니다!");
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

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }
    public FileManager getFileManager() {
        return fileManager;
    }
    public WebServerManager getWebServerManager() {
        return webServerManager;
    }

}
