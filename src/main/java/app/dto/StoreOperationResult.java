package app.dto;

import app.enam.StoreOperationType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreOperationResult {
    @JsonProperty("operationType")
    private StoreOperationType operationName;
    @JsonProperty("accountId")
    private Long accountId;
    @JsonProperty("storeId")
    private Long storeId;

    public StoreOperationResult(StoreOperationType name, Long accountId, Long storeId) {
        this.operationName = name;
        this.accountId = accountId;
        this.storeId = storeId;
    }

    public StoreOperationType getOperationName() {
        return operationName;
    }

    public void setOperationName(StoreOperationType operationName) {
        this.operationName = operationName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
}