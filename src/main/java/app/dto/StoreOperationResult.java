package app.dto;

import app.enam.StoreOperationType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreOperationResult {
    @JsonProperty("operationType")
    private StoreOperationType operationName;
    @JsonProperty("username")
    private String ownerName;
    @JsonProperty("storeName")
    private String storeName;

    public StoreOperationResult(StoreOperationType name, String ownerName, String storeName) {
        this.operationName = name;
        this.ownerName = ownerName;
        this.storeName = storeName;
    }

    public StoreOperationType getOperationName() {
        return operationName;
    }

    public void setOperationName(StoreOperationType operationName) {
        this.operationName = operationName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}