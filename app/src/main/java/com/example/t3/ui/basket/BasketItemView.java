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
            // 뷰 자체 제거는 ViewModel 변경 후 Fragment에서 다시 그려지므로 생략
        });

        // 롱클릭 시 드래그 시작 (수정된 부분)
        this.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("basket_item", basketItem.getId());
            DragShadowBuilder shadow = new DragShadowBuilder(v);
            // 중요: this(현재 BasketItemView)를 localState로 전달
            v.startDragAndDrop(data, shadow, this, 0);
            return true;
        });
    }

    /**
     * Fragment에서 전달하는 콜백
     * (삭제·수량 변경 시 ViewModel 호출)
     */
    public void setParentCallback(BasketFragment.ParentCallback callback) {
        this.callback = callback;
    }

    /**
     * BasketItem 데이터를 뷰에 바인딩
     */
    public void bind(BasketItem item) {
        this.basketItem = item;
        checkboxItem.setChecked(item.isChecked());
        textName.setText(item.getProductName());
        textQuantity.setText(String.valueOf(item.getQuantity()));
        textPrice.setText(item.getFormattedPrice());
    }

    /**
     * 드래그된 후 Fragment 쪽에서 BasketItem을 꺼낼 수 있도록
     */
    public BasketItem getBasketItem() {
        return basketItem;
    }
}