package com.example.t3.ui.pending;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.t3.R;
import com.example.t3.model.PendingItem;

public class PendingItemView extends LinearLayout {

    public interface PendingCallback {
        void onApproval(PendingItem item);
        void onReject(PendingItem item);
    }

    private TextView textProductName;
    private TextView textQuantity;
    private TextView textPrice;
    private Button btnApproval;
    private Button btnReject;
    private ImageView imageProduct; // 이미지뷰 (있는 경우)

    private PendingItem pendingItem;
    private PendingCallback callback;
    private boolean imageLoadingEnabled = false; // 🎯 기본적으로 이미지 로딩 비활성화

    public PendingItemView(Context context) {
        super(context);
        init();
    }

    public PendingItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("InflateParams")
    private void init() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.basket_bottom_item_layout, this, true);

        // 뷰 바인딩
        textProductName = findViewById(R.id.edit_product_name);
        textQuantity = findViewById(R.id.edit_quantity);
        textPrice = findViewById(R.id.edit_price);
        btnApproval = findViewById(R.id.btn_my_approval);
        btnReject = findViewById(R.id.btn_my_reject);
        imageProduct = findViewById(R.id.image_product); // 이미지뷰 (없을 수도 있음)

        // 승인 버튼
        btnApproval.setOnClickListener(v -> {
            if (callback != null && pendingItem != null) {
                callback.onApproval(pendingItem);
            }
        });

        // 거부 버튼
        btnReject.setOnClickListener(v -> {
            if (callback != null && pendingItem != null) {
                callback.onReject(pendingItem);
            }
        });
    }

    public void bind(PendingItem item) {
        this.pendingItem = item;
        textProductName.setText(item.getProductName());
        textQuantity.setText(String.valueOf(item.getQuantity()));
        textPrice.setText(item.getFormattedPrice()); // getFormattedPrice() 사용

        // 🎯 이미지 로딩 제어
        if (imageLoadingEnabled) {
            loadProductImage(item.getImageUrl());
        } else {
            hideProductImage();
        }
    }

    /**
     * 이미지 로딩 활성화/비활성화 설정
     */
    public void setImageLoadingEnabled(boolean enabled) {
        this.imageLoadingEnabled = enabled;
    }

    /**
     * 상품 이미지 로딩 (활성화된 경우에만)
     */
    private void loadProductImage(String imageUrl) {
        if (imageProduct != null && imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_menu_gallery)
                            .error(R.drawable.ic_menu_gallery)
                            .centerCrop())
                    .into(imageProduct);
            imageProduct.setBackgroundColor(0x00000000);
            imageProduct.setVisibility(VISIBLE);
        }
    }

    /**
     * 이미지 숨기기
     */
    private void hideProductImage() {
        if (imageProduct != null) {
            imageProduct.setVisibility(GONE); // 완전히 숨김
        }
    }

    public void setCallback(PendingCallback callback) {
        this.callback = callback;
    }
}