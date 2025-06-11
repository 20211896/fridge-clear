package com.example.t3.model;

public class PendingItem {

    public enum Status {
        PENDING,    // 승인대기
        APPROVED,   // 승인됨 (공동장바구니)
        REJECTED    // 거부됨
    }

    private String id;
    private String productName;
    private int quantity;
    private int unitPrice;
    private String imageUrl;
    private Status status;

    public PendingItem(String id, String productName, int quantity, int unitPrice, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.status = Status.PENDING; // 기본값은 승인대기
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Status getStatus() {
        return status;
    }

    // Setters
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Helper methods
    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isApproved() {
        return status == Status.APPROVED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
    }

    public int getTotalPrice() {
        return unitPrice * quantity;
    }

    public String getFormattedPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    @Override
    public String toString() {
        return "PendingItem{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", status=" + status +
                '}';
    }
}