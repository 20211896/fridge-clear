package com.example.t3.ui.basket;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.t3.R;
import com.example.t3.model.BasketItem;

public class BasketItemView extends LinearLayout {
    private CheckBox checkboxItem;
    private ImageView imageProduct;
    private TextView textName, textQuantity, textPrice;
    private Button btnMinus, btnPlus;
    private ImageView btnDelete;
    private BasketItem basketItem;
    private BasketFragment.ParentCallback callback;

    public BasketItemView(Context context) {
        super(context);
        init();
    }

    public BasketItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("InflateParams")
    private void init() {
        // 레이아웃 인플레이션
        LayoutInflater.from(getContext())
                .inflate(R.layout.basket_item_layout, this, true);

        // 뷰 바인딩
        checkboxItem = findViewById(R.id.checkbox_item);
        imageProduct = findViewById(R.id.image_product);
        textName      = findViewById(R.id.text_product_name);
        textQuantity  = findViewById(R.id.text_quantity);
        textPrice     = findViewById(R.id.text_price);
        btnMinus      = findViewById(R.id.btn_minus);
        btnPlus       = findViewById(R.id.btn_plus);
        btnDelete     = findViewById(R.id.btn_delete);

        // 수량 – 버튼
        btnMinus.setOnClickListener(v -> {
            if (basketItem.getQuantity() > 1) {
                int newQty = basketItem.getQuantity() - 1;
                basketItem.setQuantity(newQty);
                textQuantity.setText(String.valueOf(newQty));
                textPrice.setText(basketItem.getFormattedPrice());
                if (callback != null) callback.onQuantityChanged(basketItem, newQty);
            }
        });

        // 수량 + 버튼
        btnPlus.setOnClickListener(v -> {
            int newQty = basketItem.getQuantity() + 1;
            basketItem.setQuantity(newQty);
            textQuantity.setText(String.valueOf(newQty));
            textPrice.setText(basketItem.getFormattedPrice());
            if (callback != null) callback.onQuantityChanged(basketItem, newQty);
        });

        // 삭제 버튼
        btnDelete.setOnClickListener(v -> {
            if (callback != null) callback.onItemRemoved(basketItem);
        });

        // 롱클릭 시 드래그 시작
        this.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("basket_item", basketItem.getId());
            DragShadowBuilder shadow = new DragShadowBuilder(v);
            v.startDragAndDrop(data, shadow, this, 0);
            return true;
        });
    }

    /**
     * Fragment에서 전달하는 콜백
     */
    public void setParentCallback(BasketFragment.ParentCallback callback) {
        this.callback = callback;
    }

    /**
     * BasketItem 데이터를 뷰에 바인딩 (이미지 로딩 추가)
     */
    public void bind(BasketItem item) {
        this.basketItem = item;
        checkboxItem.setChecked(item.isChecked());
        textName.setText(item.getProductName());
        textQuantity.setText(String.valueOf(item.getQuantity()));
        textPrice.setText(item.getFormattedPrice());

        // 🎯 상품 이미지 로딩 추가
        loadProductImage(item.getImageUrl());
    }

    /**
     * 상품 이미지 로딩
     */
    private void loadProductImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Unsplash 이미지 로딩
            Glide.with(getContext())
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_menu_gallery)
                            .error(R.drawable.ic_menu_gallery)
                            .centerCrop())
                    .into(imageProduct);

            // 배경색 제거 (이미지가 있을 때)
            imageProduct.setBackgroundColor(0x00000000);
        } else {
            // 이미지가 없으면 기본 아이콘과 회색 배경
            imageProduct.setImageResource(R.drawable.ic_menu_gallery);
            imageProduct.setBackgroundColor(0xFF9E9E9E);
            imageProduct.setScaleType(ImageView.ScaleType.CENTER);
        }
    }

    /**
     * 드래그된 후 Fragment 쪽에서 BasketItem을 꺼낼 수 있도록
     */
    public BasketItem getBasketItem() {
        return basketItem;
    }
}