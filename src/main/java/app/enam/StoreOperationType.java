package app.enam;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StoreOperationType {

    CREATE("Создание магазина"),
    CHANGE_STORENAME("Смена названия");

    private final String name;

    StoreOperationType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
