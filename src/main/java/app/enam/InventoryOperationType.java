package app.enam;

public enum InventoryOperationType {

    BUY_PRODUCT("Покупка продукта"),
    SELL_PRODUCT("Продажа продукта"),
    ERROR_NOT_FOUND("Не найдено"),
    SERVER_ERROR("Внутренняя ошибка");

    private final String name;

    InventoryOperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
