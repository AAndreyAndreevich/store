package app.dto;

import app.enam.AccountOperationType;

public class AccountOperationResult {

    private String username;
    private AccountOperationType operationType;
    private boolean success;

    public AccountOperationResult(String username, AccountOperationType operationType, boolean success) {
        this.username = username;
        this.operationType = operationType;
        this.success = success;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AccountOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(AccountOperationType operationType) {
        this.operationType = operationType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}