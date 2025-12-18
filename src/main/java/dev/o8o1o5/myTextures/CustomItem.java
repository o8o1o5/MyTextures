package dev.o8o1o5.myTextures;

import org.bukkit.Material;

public class CustomItem {
    private final String id;
    private final Material baseItem;
    private String displayName;

    public CustomItem(String id, Material baseItem) {
        this.id = id;
        this.baseItem = baseItem;
        this.displayName = id;
    }

    public String getId() {
        return id;
    }

    public Material getBaseItem() {
        return baseItem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    // 리소스팩 경로 생성용입니다.
    public String getModelKey() {
        return "mytextures:item/custom/" + id;
    }
}
