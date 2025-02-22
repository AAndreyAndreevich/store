package app.enam;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InventoryOperationType {

    BUY_PRODUCT("Покупка продукта"),
    SELL_PRODUCT("Продажа продукта");

    private final String name;

    InventoryOperationType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
