package com.example.t3.data;

import java.util.HashMap;
import java.util.Map;

public class ProductImageManager {
    private static final Map<String, String> PRODUCT_IMAGE_MAP = new HashMap<>();

    static {
        // ====== 식량작물 ======
        PRODUCT_IMAGE_MAP.put("쌀", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400");
        PRODUCT_IMAGE_MAP.put("찹쌀", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400");
        PRODUCT_IMAGE_MAP.put("현미", "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400");
        PRODUCT_IMAGE_MAP.put("보리", "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400");
        PRODUCT_IMAGE_MAP.put("콩", "https://images.unsplash.com/photo-1597045566677-a9a1068aa0c3?w=400");
        PRODUCT_IMAGE_MAP.put("팥", "https://images.unsplash.com/photo-1584308972272-9e4e7685e80f?w=400");
        PRODUCT_IMAGE_MAP.put("옥수수", "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=400");

        // ====== 채소류 ======
        // 뿌리채소
        PRODUCT_IMAGE_MAP.put("배추", "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400");
        PRODUCT_IMAGE_MAP.put("무", "https://images.unsplash.com/photo-1595273670150-bd0c3c392e46?w=400");
        PRODUCT_IMAGE_MAP.put("당근", "https://images.unsplash.com/photo-1445282768818-728615cc3f38?w=400");
        PRODUCT_IMAGE_MAP.put("양파", "https://images.unsplash.com/photo-1508747703725-719777637510?w=400");
        PRODUCT_IMAGE_MAP.put("감자", "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400");
        PRODUCT_IMAGE_MAP.put("고구마", "https://images.unsplash.com/photo-1586190848220-4db9e3d22c64?w=400");

        // 잎채소
        PRODUCT_IMAGE_MAP.put("대파", "https://images.unsplash.com/photo-1609501676725-7186f440e4a0?w=400");
        PRODUCT_IMAGE_MAP.put("쪽파", "https://images.unsplash.com/photo-1609501676725-7186f440e4a0?w=400");
        PRODUCT_IMAGE_MAP.put("시금치", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400");
        PRODUCT_IMAGE_MAP.put("상추", "https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?w=400");
        PRODUCT_IMAGE_MAP.put("양배추", "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400");
        PRODUCT_IMAGE_MAP.put("브로콜리", "https://images.unsplash.com/photo-1459411621453-7b03977f4bfc?w=400");
        PRODUCT_IMAGE_MAP.put("깻잎", "https://images.unsplash.com/photo-1609501676725-7186f440e4a0?w=400");

        // 과채류
        PRODUCT_IMAGE_MAP.put("오이", "https://images.unsplash.com/photo-1449300079323-02e209d9d3a6?w=400");
        PRODUCT_IMAGE_MAP.put("호박", "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=400");
        PRODUCT_IMAGE_MAP.put("토마토", "https://images.unsplash.com/photo-1546470427-e3686eecc4ac?w=400");
        PRODUCT_IMAGE_MAP.put("가지", "https://images.unsplash.com/photo-1434627293761-8e7f5a6e79bc?w=400");
        PRODUCT_IMAGE_MAP.put("피망", "https://images.unsplash.com/photo-1525607551862-4d197d17c92a?w=400");
        PRODUCT_IMAGE_MAP.put("고추", "https://images.unsplash.com/photo-1583663848850-46af132dc2aa?w=400");

        // ====== 과일류 ======
        PRODUCT_IMAGE_MAP.put("사과", "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400");
        PRODUCT_IMAGE_MAP.put("배", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400");
        PRODUCT_IMAGE_MAP.put("감귤", "https://images.unsplash.com/photo-1557800636-894a64c1696f?w=400");
        PRODUCT_IMAGE_MAP.put("오렌지", "https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=400");
        PRODUCT_IMAGE_MAP.put("레몬", "https://images.unsplash.com/photo-1506976785307-8732e854ad03?w=400");
        PRODUCT_IMAGE_MAP.put("자몽", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400");
        PRODUCT_IMAGE_MAP.put("바나나", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400");
        PRODUCT_IMAGE_MAP.put("딸기", "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=400");
        PRODUCT_IMAGE_MAP.put("포도", "https://images.unsplash.com/photo-1537640538966-79f369143f8f?w=400");
        PRODUCT_IMAGE_MAP.put("수박", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400");
        PRODUCT_IMAGE_MAP.put("참외", "https://images.unsplash.com/photo-1600191572689-ec3cf14aeb43?w=400");
        PRODUCT_IMAGE_MAP.put("멜론", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400");
        PRODUCT_IMAGE_MAP.put("복숭아", "https://images.unsplash.com/photo-1629828874514-d71b93d92bf4?w=400");
        PRODUCT_IMAGE_MAP.put("자두", "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400");
        PRODUCT_IMAGE_MAP.put("체리", "https://images.unsplash.com/photo-1528821128474-27f963b062bf?w=400");
        PRODUCT_IMAGE_MAP.put("키위", "https://images.unsplash.com/photo-1585059895524-72359e06133a?w=400");
        PRODUCT_IMAGE_MAP.put("망고", "https://images.unsplash.com/photo-1553279768-865429fa0078?w=400");

        // ====== 축산물 ======
        PRODUCT_IMAGE_MAP.put("쇠고기", "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400");
        PRODUCT_IMAGE_MAP.put("돼지고기", "https://images.unsplash.com/photo-1602470520998-f4a52199a3d6?w=400");
        PRODUCT_IMAGE_MAP.put("닭고기", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400");
        PRODUCT_IMAGE_MAP.put("오리고기", "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400");
        PRODUCT_IMAGE_MAP.put("달걀", "https://images.unsplash.com/photo-1518492104633-130d0cc84637?w=400");
        PRODUCT_IMAGE_MAP.put("우유", "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400");

        // ====== 수산물 ======
        PRODUCT_IMAGE_MAP.put("고등어", "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=400");
        PRODUCT_IMAGE_MAP.put("갈치", "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=400");
        PRODUCT_IMAGE_MAP.put("명태", "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=400");
        PRODUCT_IMAGE_MAP.put("조기", "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=400");
        PRODUCT_IMAGE_MAP.put("오징어", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
        PRODUCT_IMAGE_MAP.put("새우", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
        PRODUCT_IMAGE_MAP.put("게", "https://images.unsplash.com/photo-1545816255-0b5faba6b3ea?w=400");
        PRODUCT_IMAGE_MAP.put("전복", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
        PRODUCT_IMAGE_MAP.put("굴", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
        PRODUCT_IMAGE_MAP.put("연어", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
        PRODUCT_IMAGE_MAP.put("참치", "https://images.unsplash.com/photo-1565680018434-b513d5e5fd47?w=400");
    }

    /**
     * 상품명으로 이미지 URL 가져오기
     * @param itemName 상품명 (예: "사과", "배추", "쇠고기")
     * @param category 카테고리 (fallback용)
     * @return 이미지 URL
     */
    public static String getImageUrl(String itemName, String category) {
        // 1순위: 상품명으로 정확한 이미지 찾기
        String imageUrl = PRODUCT_IMAGE_MAP.get(itemName);
        if (imageUrl != null) {
            return imageUrl;
        }

        // 2순위: 카테고리로 기본 이미지 반환
        return getDefaultImageByCategory(category);
    }

    /**
     * 카테고리별 기본 이미지 URL
     */
    private static String getDefaultImageByCategory(String category) {
        switch (category) {
            case "식량작물":
                return "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400"; // 쌀
            case "채소류":
                return "https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400"; // 배추
            case "과일류":
                return "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400"; // 사과
            case "축산물":
                return "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400"; // 쇠고기
            case "수산물":
                return "https://images.unsplash.com/photo-1544943910-4c1dc44aab44?w=400"; // 생선
            default:
                return "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400"; // 기본 이미지
        }
    }

    /**
     * 새로운 상품 이미지 추가
     */
    public static void addProductImage(String itemName, String imageUrl) {
        PRODUCT_IMAGE_MAP.put(itemName, imageUrl);
    }

    /**
     * 등록된 상품 개수 확인
     */
    public static int getRegisteredProductCount() {
        return PRODUCT_IMAGE_MAP.size();
    }
}