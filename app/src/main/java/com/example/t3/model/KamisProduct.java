package com.example.t3.model;

public class KamisProduct {
    private String itemName;      // 품목명
    private String kindName;      // 품종명
    private String rankName;      // 등급명
    private String unitName;      // 단위명
    private String dayTime;       // 조사일자
    private String dpr1;          // 당일가격
    private String dpr2;          // 1개월전가격
    private String dpr3;          // 1년전가격
    private String dpr4;          // 평년가격
    private String direction;     // 등락구분
    private String categoryName;  // 부류명
    private String countyName;    // 조사지역명
    private String marketName;    // 시장명
    private String imageUrl;      // 이미지 URL 추가 ⭐

    // 생성자
    public KamisProduct() {}

    public KamisProduct(String itemName, String kindName, String rankName,
                        String unitName, String dpr1, String categoryName) {
        this.itemName = itemName;
        this.kindName = kindName;
        this.rankName = rankName;
        this.unitName = unitName;
        this.dpr1 = dpr1;
        this.categoryName = categoryName;
    }

    // Getter 메소드들
    public String getItemName() { return itemName; }
    public String getKindName() { return kindName; }
    public String getRankName() { return rankName; }
    public String getUnitName() { return unitName; }
    public String getDayTime() { return dayTime; }
    public String getDpr1() { return dpr1; }
    public String getDpr2() { return dpr2; }
    public String getDpr3() { return dpr3; }
    public String getDpr4() { return dpr4; }
    public String getDirection() { return direction; }
    public String getCategoryName() { return categoryName; }
    public String getCountyName() { return countyName; }
    public String getMarketName() { return marketName; }
    public String getImageUrl() { return imageUrl; } // 추가 ⭐

    // Setter 메소드들
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setKindName(String kindName) { this.kindName = kindName; }
    public void setRankName(String rankName) { this.rankName = rankName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public void setDayTime(String dayTime) { this.dayTime = dayTime; }
    public void setDpr1(String dpr1) { this.dpr1 = dpr1; }
    public void setDpr2(String dpr2) { this.dpr2 = dpr2; }
    public void setDpr3(String dpr3) { this.dpr3 = dpr3; }
    public void setDpr4(String dpr4) { this.dpr4 = dpr4; }
    public void setDirection(String direction) { this.direction = direction; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setCountyName(String countyName) { this.countyName = countyName; }
    public void setMarketName(String marketName) { this.marketName = marketName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // 추가 ⭐

    // 가격 관련 편의 메소드들
    public double getPriceAsDouble() {
        try {
            return dpr1 != null ? Double.parseDouble(dpr1.replaceAll(",", "")) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String getFormattedPrice() {
        try {
            double price = getPriceAsDouble();
            return String.format("%,d원", (int)price);
        } catch (Exception e) {
            return dpr1 != null ? dpr1 + "원" : "가격정보없음";
        }
    }

    public String getFullName() {
        String full = itemName;
        if (kindName != null && !kindName.isEmpty()) {
            full += " (" + kindName + ")";
        }
        return full;
    }
}