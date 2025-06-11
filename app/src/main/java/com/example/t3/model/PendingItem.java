package com.example.t3.model;

public class PendingItem {
    // 상태를 나타내는 enum 추가
    public enum Status {
        PENDING,    // 승인대기
        APPROVED    // 승인됨 (공동장바구니)
    }

    private String id;
    private String productName;
    private int quantity;
    private int unitPrice;
    private String imageUrl;
    private long timestamp;
    private Status status; // 새로 추가된 필드

    public PendingItem(String id, String productName, int quantity, int unitPrice, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.timestamp = System.currentTimeMillis();
        this.status = Status.PENDING; // 기본값은 승인대기
    }

    public int getTotalPrice() {
        return unitPrice * quantity;
    }

    public String getFormattedPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    // 기존 Getters & Setters
    public String getId() { return id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }

    // 새로 추가된 Status 관련 메서드들
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    // 편의 메서드들
    public boolean isPending() { return status == Status.PENDING; }
    public boolean isApproved() { return status == Status.APPROVED; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PendingItem that = (PendingItem) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}