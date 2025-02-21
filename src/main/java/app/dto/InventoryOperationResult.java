package app.dto;

import app.enam.InventoryOperationType;

import java.math.BigDecimal;

public class InventoryOperationResult {

    private InventoryOperationType operationName;
    private BigDecimal remainingBalance;
    private String productName;
    private Integer quantity;
    private String ownerName;
    private String storeName;
    private boolean success;

    public InventoryOperationResult(InventoryOperationType operationName, BigDecimal remainingBalance, String productName,
                                    Integer quantity, String ownerName, String storeName, boolean success) {
        this.operationName = operationName;
        this.remainingBalance = remainingBalance;
        this.productName = productName;
        this.quantity = quantity;
        this.ownerName = ownerName;
        this.storeName = storeName;
        this.success = success;
    }

    public InventoryOperationType getOperationName() {
        return operationName;
    }

    public void setOperationName(InventoryOperationType operationName) {
        this.operationName = operationName;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}