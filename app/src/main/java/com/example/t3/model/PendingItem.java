package com.example.t3.model;

public class PendingItem {
    // 상태를 나타내는 enum
    public enum Status {
        PENDING,    // 승인대기
        APPROVED,   // 승인됨 (공동장바구니)
        REJECTED    // 거부됨 (장바구니로 복귀)
    }

    private String id;
    private String productName;
    private int quantity;
    private int unitPrice;
    private String imageUrl; // 🎯 이미지 URL (표시하지 않지만 데이터 유지)
    private long timestamp;
    private Status status;

    // 🎯 생성자 - 이미지 URL 포함
    public PendingItem(String id, String productName, int quantity, int unitPrice, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl; // 이미지 URL 저장
        this.timestamp = System.currentTimeMillis();
        this.status = Status.PENDING; // 기본값은 승인대기
    }

    // 기본 생성자 (Gson용)
    public PendingItem() {
        this.timestamp = System.currentTimeMillis();
        this.status = Status.PENDING;
    }

    // 계산 메서드들
    public int getTotalPrice() {
        return unitPrice * quantity;
    }

    public String getFormattedPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    // 기본 Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    // 🎯 이미지 URL getter/setter
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Status 관련 메서드들
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // 편의 메서드들 (상태 확인)
    public boolean isPending() {
        return status == Status.PENDING;
    }

    public boolean isApproved() {
        return status == Status.APPROVED;
    }

    public boolean isRejected() {
        return status == Status.REJECTED;
    }

    // 🎯 상태 변경 편의 메서드들
    public void approve() {
        this.status = Status.APPROVED;
    }

    public void reject() {
        this.status = Status.REJECTED;
    }

    public void setPending() {
        this.status = Status.PENDING;
    }

    // equals & hashCode (ID 기준)
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

    @Override
    public String toString() {
        return "PendingItem{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", status=" + status +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}