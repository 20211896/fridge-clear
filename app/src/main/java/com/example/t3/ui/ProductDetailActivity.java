package com.example.t3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t3.data.ProductImageManager;
import com.example.t3.databinding.ActivityProductDetailBinding;
import com.example.t3.manager.BasketManager;
import com.example.t3.model.BasketItem;
import com.example.t3.model.KamisProduct;
import com.example.t3.utils.CustomToast;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private KamisProduct product;
    private BasketManager basketManager;
    private int quantity = 1;

    // 액티비티 시작용 정적 메소드
    public static void start(Context context, KamisProduct product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("product_name", product.getItemName());
        intent.putExtra("product_kind", product.getKindName());
        intent.putExtra("product_rank", product.getRankName());
        intent.putExtra("product_unit", product.getUnitName());
        intent.putExtra("product_price", product.getDpr1());
        intent.putExtra("product_category", product.getCategoryName());
        intent.putExtra("product_image", product.getImageUrl());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BasketManager 초기화
        basketManager = BasketManager.getInstance(this);

        // Intent로부터 상품 정보 받기
        loadProductFromIntent();

        // UI 설정
        setupUI();
        setupClickListeners();
    }

    private void loadProductFromIntent() {
        Intent intent = getIntent();
        product = new KamisProduct();
        product.setItemName(intent.getStringExtra("product_name"));
        product.setKindName(intent.getStringExtra("product_kind"));
        product.setRankName(intent.getStringExtra("product_rank"));
        product.setUnitName(intent.getStringExtra("product_unit"));
        product.setDpr1(intent.getStringExtra("product_price"));
        product.setCategoryName(intent.getStringExtra("product_category"));
        product.setImageUrl(intent.getStringExtra("product_image"));

        // 이미지 URL이 없으면 ProductImageManager에서 가져오기 (장바구니용)
        if (product.getImageUrl() == null || product.getImageUrl().isEmpty()) {
            String imageUrl = ProductImageManager.getImageUrl(
                    product.getItemName(),
                    product.getCategoryName()
            );
            product.setImageUrl(imageUrl);
        }
    }

    private void setupUI() {
        if (product == null) return;

        // 실제 상품 데이터로 UI 업데이트
        binding.txtProductName.setText(product.getFullName());
        binding.txtCategoryBadge.setText(product.getCategoryName());
        binding.txtCurrentPrice.setText(product.getFormattedPrice());
        binding.txtUnit.setText(product.getUnitName());
        binding.txtGrade.setText(product.getRankName());
        binding.txtItemName.setText(product.getItemName());
        binding.txtKindName.setText(product.getKindName() != null ? product.getKindName() : "-");
        binding.txtOrigin.setText("국산"); // 기본값

        // 카테고리별 배지 색상 설정
        setCategoryBadgeColor(product.getCategoryName());

        // 상품 설명 생성
        generateProductDescription();

        // 초기 수량과 총 가격 설정
        updateQuantityAndPrice();

        // 🎯 이미지 로딩 제거 (레이아웃에 ImageView가 없으므로)
        // loadProductImage(); // 주석 처리
    }

    private void setCategoryBadgeColor(String category) {
        int color;
        switch (category) {
            case "식량작물":
                color = 0xFFFFEB3B; // 노란색
                break;
            case "채소류":
                color = 0xFF4CAF50; // 초록색
                break;
            case "과일류":
                color = 0xFFFF9800; // 주황색
                break;
            case "축산물":
                color = 0xFFF44336; // 빨간색
                break;
            case "수산물":
                color = 0xFF2196F3; // 파란색
                break;
            default:
                color = 0xFF9E9E9E; // 회색
                break;
        }
        binding.txtCategoryBadge.setBackgroundColor(color);
    }

    private void generateProductDescription() {
        String description = getProductDescription(product.getItemName(), product.getKindName());
        binding.txtProductDescription.setText(description);
    }

    private String getProductDescription(String itemName, String kindName) {
        // 상품별 설명 생성
        switch (itemName) {
            case "사과":
                return "신선하고 아삭한 " + (kindName != null ? kindName : "") + " 사과입니다. " +
                        "당도가 높고 과즙이 풍부하여 많은 분들이 선호하는 품종입니다. " +
                        "비타민C와 식이섬유가 풍부하여 건강에도 좋습니다.";
            case "배추":
                return "싱싱하고 단단한 " + (kindName != null ? kindName : "") + " 배추입니다. " +
                        "김치 담그기에 최적화된 품질로, 아삭한 식감과 단맛이 특징입니다.";
            case "당근":
                return "달콤하고 영양가 높은 " + (kindName != null ? kindName : "") + " 당근입니다. " +
                        "베타카로틴이 풍부하여 눈 건강에 좋으며, 다양한 요리에 활용 가능합니다.";
            case "토마토":
                return "신선하고 맛있는 " + (kindName != null ? kindName : "") + " 토마토입니다. " +
                        "리코펜이 풍부하여 항산화 효과가 뛰어나며, 생식용과 요리용 모두 적합합니다.";
            case "쇠고기":
                return "신선하고 고품질의 " + (kindName != null ? kindName : "") + " 쇠고기입니다. " +
                        "단백질이 풍부하고 육질이 부드러워 다양한 요리에 활용하기 좋습니다.";
            case "고등어":
                return "신선한 " + (kindName != null ? kindName : "") + " 고등어입니다. " +
                        "오메가3가 풍부하여 건강에 좋으며, 구이나 조림 요리에 최적입니다.";
            case "감자":
                return "포슬포슬한 " + (kindName != null ? kindName : "") + " 감자입니다. " +
                        "다양한 요리에 활용 가능하며, 탄수화물과 칼륨이 풍부합니다.";
            case "양파":
                return "매콤하고 달콤한 " + (kindName != null ? kindName : "") + " 양파입니다. " +
                        "각종 요리의 기본 재료로 활용되며, 항산화 성분이 풍부합니다.";
            case "돼지고기":
                return "신선한 " + (kindName != null ? kindName : "") + " 돼지고기입니다. " +
                        "단백질이 풍부하고 부드러운 육질로 다양한 요리에 적합합니다.";
            case "닭고기":
                return "신선한 " + (kindName != null ? kindName : "") + " 닭고기입니다. " +
                        "저지방 고단백 식품으로 건강한 식단에 좋습니다.";
            default:
                return "신선하고 품질 좋은 " + itemName + "입니다. " +
                        (kindName != null ? kindName + " 품종으로 " : "") +
                        "엄선된 재료로 안심하고 드실 수 있습니다.";
        }
    }

    private void setupClickListeners() {
        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener(v -> finish());

        // 수량 감소 버튼
        binding.btnQuantityMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndPrice();
            }
        });

        // 수량 증가 버튼
        binding.btnQuantityPlus.setOnClickListener(v -> {
            if (quantity < 99) {
                quantity++;
                updateQuantityAndPrice();
            }
        });

        // 장바구니 추가 버튼
        binding.btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateQuantityAndPrice() {
        binding.txtQuantity.setText(String.valueOf(quantity));

        double unitPrice = product.getPriceAsDouble();
        double totalPrice = unitPrice * quantity;

        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.KOREA);
        binding.txtTotalPrice.setText("총 " + fmt.format((int)totalPrice) + "원");
    }

    /**
     * 🎯 이미지 URL을 포함한 장바구니 추가 (이미지는 장바구니에서만 표시)
     */
    private void addToCart() {
        // BasketItem 생성 (이미지 URL 포함 - 장바구니에서 사용)
        String id = String.valueOf(System.currentTimeMillis());
        String name = product.getFullName();
        int unitPrice = (int) Math.round(product.getPriceAsDouble());
        String imageUrl = product.getImageUrl(); // 🎯 이미지 URL은 여전히 포함 (장바구니용)

        BasketItem basketItem = new BasketItem(id, name, unitPrice, quantity, imageUrl);

        // BasketManager에 추가
        basketManager.addMyBasketItem(basketItem);

        // 상세한 성공 메시지 표시
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.KOREA);
        CustomToast.show(this,
                "🛒 " + name + "\n" +
                        "수량: " + quantity + "개\n" +
                        "총 " + fmt.format(unitPrice * quantity) + "원\n" +
                        "장바구니에 추가되었습니다!");

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}