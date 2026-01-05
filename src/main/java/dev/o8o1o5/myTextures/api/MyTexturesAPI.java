package dev.o8o1o5.myTextures.api;

import dev.o8o1o5.myTextures.MyTextures;
import org.bukkit.inventory.ItemStack;

/**
 * 외부 플러그인(예: MyEconomy)에서 MyTextures의 기능을
 * 코드 수준에서 호출할 수 있도록 제공하는 정적 API 클래스입니다.
 */
public class MyTexturesAPI {
    private static MyTextures plugin;

    /**
     * MyTextures 플러그인이 활성화될 때 인스턴스를 초기화합니다.
     */
    public static void init(MyTextures instance) {
        plugin = instance;
    }

    /**
     * 외부 플러그인에서 정의한 ItemBuilder를 MyTextures 시스템에 등록합니다.
     * 등록된 아이템은 MyTextures의 리소스 관리 시스템과 연동됩니다.
     * * @param builder 아이템의 상세 설정이 담긴 ItemBuilder 객체
     */
    public static void registerItem(TexturesItemBuilder builder) {
        if (plugin != null) {
            // 개편된 ItemRegistry의 register 메서드 호출
            plugin.getItemRegistry().register(builder);
            // 등록 후 파일에 영구 저장 (선택 사항)
            plugin.getItemRegistry().saveItems();
        }
    }

    /**
     * 등록된 ID를 바탕으로 ItemStack을 생성합니다.
     * MyTextures의 ItemModel(리소스팩 모델)이 자동으로 적용됩니다.
     * * @param id 생성할 아이템의 ID
     * @return 생성된 ItemStack 객체 (등록되지 않은 ID일 경우 null)
     */
    public static ItemStack createItem(String id) {
        if (plugin == null) return null;

        // 개편된 ItemRegistry의 createItem 메서드 호출
        return plugin.getItemRegistry().createItem(id);
    }

    /**
     * 아이템의 MyTextuers Id를 가져옵니다.
     * @param item 확인할 아이템
     * @return 아이템 id (MyTextures 아이템이 아니면 null
     */
    public static String getItemId(ItemStack item) {
        if (plugin == null || item == null) return null;
        return plugin.getItemRegistry().getIdFromItem(item);
    }

    /**
     * 특정 ID의 아이템이 이미 등록되어 있는지 확인합니다.
     */
    public static boolean exists(String id) {
        if (plugin == null) return false;
        return plugin.getItemRegistry().exists(id);
    }

    public static void registerModule(TexturesModule module) {
        if (plugin == null) return;

        plugin.getLogger().info("[" + module.getModuleName() + "] 모듈의 아이템들이 등록됐습니다.");
    }
}