package dev.o8o1o5.myTextures.manager;

import dev.o8o1o5.myTextures.MyTextures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private final MyTextures plugin;

    public  FileManager(MyTextures plugin) {
        this.plugin = plugin;
    }

    // 폴더 구조 초기화
    public void setupFolders() {
        String[] paths = {
                "images",
                "export/assets/mytextures/items",
                "export/assets/mytextures/models/item/custom",
                "export/assets/mytextures/textures/item"
        };
        for (String path : paths) {
            File folder = new File(plugin.getDataFolder(), path);
            if (!folder.exists()) folder.mkdirs();
        }
    }

    // pack.mcmeta 생성
    public void createPackMeta() {
        File file = new File(plugin.getDataFolder(), "export/pack.mcmeta");
        String content = "{\n" +
                "    \"pack\": {\n" +
                "        \"description\": \"MyTextures Auto Generated Pack\",\n" +
                "        \"min_format\": 69,\n" +
                "        \"max_format\": 69\n" +
                "    }\n" +
                "}";
        writeFile(file, content);
    }

    // 실제 리소스 파일들 생성 (Register 시 호출)
    public boolean generateResourceFiles(String id) {
        File sourceImage = new File(plugin.getDataFolder(), "images/" + id + ".png");
        if (!sourceImage.exists()) return false;

        try {
            // 1. 이미지 복사
            File destImage = new File(plugin.getDataFolder(), "export/assets/mytextures/textures/item/" + id + ".png");
            Files.copy(sourceImage.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Item Definition (JSON) 생성
            writeFile(new File(plugin.getDataFolder(), "export/assets/mytextures/items/" + id + ".json"),
                    "{\"model\": {\"type\": \"minecraft:model\", \"model\": \"mytextures:item/custom/" + id + "\"}}");

            // 3. Model (JSON) 생성
            writeFile(new File(plugin.getDataFolder(), "export/assets/mytextures/models/item/custom/" + id + ".json"),
                    "{\"parent\": \"minecraft:item/generated\", \"textures\": {\"layer0\": \"mytextures:item/" + id + "\"}}");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
