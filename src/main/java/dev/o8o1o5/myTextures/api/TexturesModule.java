package dev.o8o1o5.myTextures.api;

public abstract class TexturesModule {

    private final String moduleName;

    public TexturesModule(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * 아이템들을 등록하는 로직을 여기에 구현합니다.
     */
    public abstract void onRegister();

    /**
     * 모듈 이름을 반환합니다.
     */
    public String getModuleName() {
        return moduleName;
    }

    protected void register(TexturesItemBuilder builder) {
        MyTexturesAPI.registerItem(builder);
    }
}
