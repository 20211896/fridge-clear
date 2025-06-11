package com.example.t3.model;

public class BasketItem {
    private String id;
    private String productName;
    private int quantity;
    private int unitPrice;    // 단가(원)
    private String imageUrl;
    private boolean isChecked;

    public BasketItem(String id, String productName, int unitPrice, int quantity, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.isChecked = true;
    }

    // 총액 반환
    public int getTotalPrice() {
        return unitPrice * quantity;
    }

    // 문자열로 포맷팅된 총액 (ex. "1,200원")
    public String getFormattedPrice() {
        return String.format("%,d원", getTotalPrice());
    }

    // --- getters & setters ---
    public String getId() { return id; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
    public String getImageUrl() { return imageUrl; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}
