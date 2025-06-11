package com.example.t3.data;

import com.example.t3.model.KamisProduct;
import java.util.ArrayList;
import java.util.List;

public class KamisDataManager {

    public static List<KamisProduct> getKamisProducts() {
        List<KamisProduct> products = new ArrayList<>();

        // ====== 식량작물 ======
        addProduct(products, "쌀", "일반계", "상품", "20kg", "62,000", "식량작물");
        addProduct(products, "찹쌀", "일반계", "상품", "20kg", "72,000", "식량작물");
        addProduct(products, "현미", "일반계", "상품", "20kg", "68,000", "식량작물");
        addProduct(products, "보리", "쌀보리", "상품", "20kg", "45,000", "식량작물");
        addProduct(products, "콩", "백태", "상품", "35kg", "280,000", "식량작물");
        addProduct(products, "팥", "적두", "상품", "40kg", "420,000", "식량작물");
        addProduct(products, "옥수수", "찰옥수수", "상품", "10kg", "25,000", "식량작물");

        // ====== 채소류 ======
        // 뿌리채소
        addProduct(products, "배추", "고랭지", "상품", "10kg", "8,500", "채소류");
        addProduct(products, "배추", "일반배추", "상품", "10kg", "7,200", "채소류");
        addProduct(products, "무", "일반무", "상품", "20kg", "12,000", "채소류");
        addProduct(products, "무", "알타리무", "상품", "4kg", "6,800", "채소류");
        addProduct(products, "당근", "무세척", "상품", "20kg", "28,000", "채소류");
        addProduct(products, "당근", "세척", "상품", "10kg", "18,000", "채소류");
        addProduct(products, "양파", "일반양파", "상품", "20kg", "15,600", "채소류");
        addProduct(products, "양파", "햇양파", "상품", "20kg", "18,200", "채소류");
        addProduct(products, "감자", "수미", "상품", "20kg", "18,400", "채소류");
        addProduct(products, "감자", "대지마", "상품", "20kg", "22,000", "채소류");
        addProduct(products, "고구마", "밤고구마", "상품", "10kg", "32,000", "채소류");
        addProduct(products, "고구마", "호박고구마", "상품", "10kg", "28,000", "채소류");

        // 잎채소류
        addProduct(products, "대파", "일반대파", "상품", "1kg", "3,200", "채소류");
        addProduct(products, "쪽파", "일반쪽파", "상품", "1kg", "4,800", "채소류");
        addProduct(products, "시금치", "일반시금치", "상품", "4kg", "8,800", "채소류");
        addProduct(products, "시금치", "물시금치", "상품", "4kg", "7,200", "채소류");
        addProduct(products, "상추", "청상추", "상품", "4kg", "12,600", "채소류");
        addProduct(products, "상추", "적상추", "상품", "4kg", "15,200", "채소류");
        addProduct(products, "양배추", "일반양배추", "상품", "8kg", "9,600", "채소류");
        addProduct(products, "브로콜리", "일반브로콜리", "상품", "4kg", "18,000", "채소류");
        addProduct(products, "깻잎", "일반깻잎", "상품", "2kg", "12,000", "채소류");

        // 과채류
        addProduct(products, "오이", "가시오이", "상품", "10kg", "18,500", "채소류");
        addProduct(products, "오이", "다다기오이", "상품", "10kg", "22,000", "채소류");
        addProduct(products, "호박", "애호박", "상품", "20kg", "22,000", "채소류");
        addProduct(products, "호박", "단호박", "상품", "10kg", "18,000", "채소류");
        addProduct(products, "토마토", "일반토마토", "상품", "10kg", "32,500", "채소류");
        addProduct(products, "토마토", "방울토마토", "상품", "5kg", "28,000", "채소류");
        addProduct(products, "가지", "일반가지", "상품", "4kg", "8,200", "채소류");
        addProduct(products, "피망", "일반피망", "상품", "4kg", "15,600", "채소류");
        addProduct(products, "고추", "청고추", "상품", "4kg", "18,000", "채소류");
        addProduct(products, "고추", "홍고추", "상품", "4kg", "22,000", "채소류");

        // ====== 과일류 ======
        addProduct(products, "사과", "후지", "상품", "10kg", "45,000", "과일류");
        addProduct(products, "사과", "홍로", "상품", "10kg", "38,000", "과일류");
        addProduct(products, "사과", "갈라", "상품", "10kg", "42,000", "과일류");
        addProduct(products, "배", "신고", "상품", "15kg", "52,000", "과일류");
        addProduct(products, "배", "원황", "상품", "15kg", "48,000", "과일류");
        addProduct(products, "배", "추황배", "상품", "15kg", "55,000", "과일류");

        // 감귤류
        addProduct(products, "감귤", "노지감귤", "상품", "10kg", "25,000", "과일류");
        addProduct(products, "감귤", "하우스감귤", "상품", "5kg", "18,000", "과일류");
        addProduct(products, "오렌지", "네이블오렌지", "상품", "10kg", "32,000", "과일류");
        addProduct(products, "레몬", "수입레몬", "상품", "5kg", "28,000", "과일류");
        addProduct(products, "자몽", "수입자몽", "상품", "10kg", "35,000", "과일류");

        // 기타 과일
        addProduct(products, "바나나", "수입바나나", "상품", "13kg", "28,000", "과일류");
        addProduct(products, "딸기", "매향", "상품", "2kg", "16,800", "과일류");
        addProduct(products, "딸기", "설향", "상품", "2kg", "18,500", "과일류");
        addProduct(products, "포도", "캠벨얼리", "상품", "5kg", "18,500", "과일류");
        addProduct(products, "포도", "거봉", "상품", "2kg", "15,000", "과일류");
        addProduct(products, "포도", "샤인머스캣", "상품", "2kg", "35,000", "과일류");
        addProduct(products, "수박", "일반수박", "상품", "1개", "12,000", "과일류");
        addProduct(products, "수박", "소형수박", "상품", "1개", "8,500", "과일류");
        addProduct(products, "참외", "일반참외", "상품", "8kg", "24,000", "과일류");
        addProduct(products, "멜론", "머스크멜론", "상품", "8kg", "45,000", "과일류");
        addProduct(products, "복숭아", "백도", "상품", "5kg", "28,000", "과일류");
        addProduct(products, "자두", "대석조생", "상품", "5kg", "22,000", "과일류");
        addProduct(products, "체리", "수입체리", "상품", "2kg", "48,000", "과일류");
        addProduct(products, "키위", "국산키위", "상품", "5kg", "32,000", "과일류");
        addProduct(products, "망고", "수입망고", "상품", "5kg", "65,000", "과일류");

        // ====== 축산물 ======
        addProduct(products, "쇠고기", "한우갈비", "1등급", "1kg", "65,000", "축산물");
        addProduct(products, "쇠고기", "한우등심", "1등급", "1kg", "85,000", "축산물");
        addProduct(products, "쇠고기", "한우안심", "1등급", "1kg", "95,000", "축산물");
        addProduct(products, "쇠고기", "수입쇠고기", "상품", "1kg", "28,000", "축산물");
        addProduct(products, "돼지고기", "삼겹살", "상품", "1kg", "18,500", "축산물");
        addProduct(products, "돼지고기", "목살", "상품", "1kg", "14,200", "축산물");
        addProduct(products, "돼지고기", "앞다리살", "상품", "1kg", "12,800", "축산물");
        addProduct(products, "돼지고기", "등갈비", "상품", "1kg", "16,500", "축산물");
        addProduct(products, "닭고기", "육계", "상품", "1kg", "3,800", "축산물");
        addProduct(products, "닭고기", "영계", "상품", "1kg", "4,200", "축산물");
        addProduct(products, "오리고기", "육용오리", "상품", "1kg", "6,800", "축산물");
        addProduct(products, "달걀", "특란", "상품", "30개", "8,200", "축산물");
        addProduct(products, "달걀", "대란", "상품", "30개", "7,800", "축산물");
        addProduct(products, "우유", "일반우유", "상품", "1L", "2,800", "축산물");

        // ====== 수산물 ======
        addProduct(products, "고등어", "일반고등어", "상품", "1kg", "8,500", "수산물");
        addProduct(products, "고등어", "노르웨이고등어", "상품", "1kg", "12,000", "수산물");
        addProduct(products, "갈치", "국산갈치", "상품", "1kg", "18,000", "수산물");
        addProduct(products, "갈치", "냉동갈치", "상품", "1kg", "12,000", "수산물");
        addProduct(products, "명태", "냉동명태", "상품", "1kg", "6,800", "수산물");
        addProduct(products, "명태", "생명태", "상품", "1kg", "9,200", "수산물");
        addProduct(products, "조기", "참조기", "상품", "1kg", "15,000", "수산물");
        addProduct(products, "오징어", "일반오징어", "상품", "1kg", "15,600", "수산물");
        addProduct(products, "오징어", "마른오징어", "상품", "1kg", "28,000", "수산물");
        addProduct(products, "새우", "흰다리새우", "상품", "1kg", "22,000", "수산물");
        addProduct(products, "새우", "대하", "상품", "1kg", "45,000", "수산물");
        addProduct(products, "게", "꽃게", "상품", "1kg", "35,000", "수산물");
        addProduct(products, "전복", "국산전복", "상품", "1kg", "85,000", "수산물");
        addProduct(products, "굴", "생굴", "상품", "1kg", "12,000", "수산물");
        addProduct(products, "연어", "생연어", "상품", "1kg", "28,000", "수산물");
        addProduct(products, "참치", "생참치", "상품", "1kg", "55,000", "수산물");

        return products;
    }

    private static void addProduct(List<KamisProduct> products, String itemName, String kindName,
                                   String rankName, String unitName, String price, String category) {
        KamisProduct product = new KamisProduct(itemName, kindName, rankName, unitName, price, category);
        // 🎯 상품명 기반 자동 이미지 매핑
        String imageUrl = ProductImageManager.getImageUrl(itemName, category);
        product.setImageUrl(imageUrl);
        products.add(product);
    }

    // 나머지 메소드들은 동일...
    public static List<String> getKamisCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("전체");
        categories.add("식량작물");
        categories.add("채소류");
        categories.add("과일류");
        categories.add("축산물");
        categories.add("수산물");
        return categories;
    }

    public static List<KamisProduct> getProductsByCategory(String category) {
        List<KamisProduct> allProducts = getKamisProducts();
        if ("전체".equals(category)) {
            return allProducts;
        }

        List<KamisProduct> filtered = new ArrayList<>();
        for (KamisProduct product : allProducts) {
            if (product.getCategoryName().equals(category)) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    public static List<KamisProduct> searchProducts(String query) {
        List<KamisProduct> allProducts = getKamisProducts();
        if (query == null || query.trim().isEmpty()) {
            return allProducts;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<KamisProduct> filtered = new ArrayList<>();

        for (KamisProduct product : allProducts) {
            if (product.getItemName().toLowerCase().contains(lowerQuery) ||
                    (product.getKindName() != null && product.getKindName().toLowerCase().contains(lowerQuery)) ||
                    product.getCategoryName().toLowerCase().contains(lowerQuery)) {
                filtered.add(product);
            }
        }
        return filtered;
    }

    public static List<KamisProduct> searchProductsByCategory(String category, String query) {
        List<KamisProduct> categoryProducts = getProductsByCategory(category);

        if (query == null || query.trim().isEmpty()) {
            return categoryProducts;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<KamisProduct> filtered = new ArrayList<>();

        for (KamisProduct product : categoryProducts) {
            if (product.getItemName().toLowerCase().contains(lowerQuery) ||
                    (product.getKindName() != null && product.getKindName().toLowerCase().contains(lowerQuery))) {
                filtered.add(product);
            }
        }
        return filtered;
    }
}