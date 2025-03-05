package app.enam;

public enum AccountOperationType {

    REGISTRATION_ACCOUNT("Регистрация аккаунта"),
    LOG_IN("Авторизация");

    private final String name;

    AccountOperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}