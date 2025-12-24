package dev.o8o1o5.myTextures.manager;

import dev.o8o1o5.myTextures.MyTextures;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public void zipResourcePack() {
        File exportFolder = new File(plugin.getDataFolder(), "export");
        File zipFile = new File(plugin.getDataFolder(), "resourcepack.zip");

        if (zipFile.exists()) zipFile.delete();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            Path sourcePath = exportFolder.toPath();

            // Files.walk를 사용하여 내부의 모든 파일을 순회
            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        // 1. 경로에서 'export' 폴더를 제외한 상대 경로 추출
                        String entryName = sourcePath.relativize(path).toString();

                        // 2. 윈도우 경로 구분자(\)를 마인크래프트 표준(/)으로 변경 (가장 중요!)
                        entryName = entryName.replace("\\", "/");

                        // 3. 마인크래프트 규격에 따라 모든 파일 경로를 소문자로 변환 (권장)
                        entryName = entryName.toLowerCase();

                        ZipEntry zipEntry = new ZipEntry(entryName);

                        // 4. 압축 방식 설정 (표준 Deflate)
                        zipEntry.setMethod(ZipEntry.DEFLATED);

                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            plugin.getLogger().warning("파일 압축 중 오류 발생: " + entryName);
                        }
                    });

            plugin.getLogger().info("성공적으로 resourcepack.zip 파일이 생성되었습니다.");
        } catch (IOException e) {
            plugin.getLogger().severe("리소스팩 압축 중 치명적 오류 발생!");
            e.printStackTrace();
        }
    }

    public void deleteResourceFiles(String id) {
        File itemJson = new File(plugin.getDataFolder(), "export/assets/mytextures/items/" + id + ".json");
        File modelJson = new File(plugin.getDataFolder(), "export/assets/mytextures/models/item/custom/" + id + ".json");
        File texture = new File(plugin.getDataFolder(), "export/assets/mytextures/textures/item/" + id + ".png");

        if (itemJson.exists()) itemJson.delete();
        if (modelJson.exists()) modelJson.delete();
        if (texture.exists()) texture.delete();
    }

    private void writeFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getResourcePackHash() {
        File zipFile = new File(plugin.getDataFolder(), "resourcepack.zip");
        if (!zipFile.exists()) return null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream is =  new FileInputStream(zipFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);
                }
            }
            return digest.digest(); // 20바이트 SHA-1 해시 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
