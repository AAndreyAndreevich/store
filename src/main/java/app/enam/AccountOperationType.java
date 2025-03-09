package app.enam;

public enum AccountOperationType {

    REGISTRATION_ACCOUNT("Регистрация аккаунта"),
    LOG_IN("Авторизация"),
    CHANGE_USERNAME("Смена имени"),
    CHANGE_PASSWORD("Смена пароля");

    private final String name;

    AccountOperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}