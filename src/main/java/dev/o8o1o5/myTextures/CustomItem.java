package dev.o8o1o5.myTextures;

import org.bukkit.Material;

public class CustomItem {
    private final String id;
    private final Material baseItem;

    public CustomItem(String id, Material baseItem) {
        this.id = id;
        this.baseItem = baseItem;
    }

    public String getId() {
        return id;
    }

    public Material getBaseItem() {
        return baseItem;
    }

    // 리소스팩 경로 생성용입니다.
    public String getModelkey() {
        return "mytextures:item/" + id;
    }
}
