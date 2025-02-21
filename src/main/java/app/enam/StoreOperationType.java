package app.enam;

public enum StoreOperationType {

    CREATE("Создание магазина"),
    DELETE("Удаление магазина");

    private final String name;

    StoreOperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
