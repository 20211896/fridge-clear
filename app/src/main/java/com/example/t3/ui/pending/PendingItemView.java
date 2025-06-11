package com.example.t3.ui.pending;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.t3.R;
import com.example.t3.model.PendingItem;

public class PendingItemView extends LinearLayout {

    public interface PendingCallback {
        void onApproval(PendingItem item);
        void onReject(PendingItem item);
    }

    private TextView textProductName;  // EditText에서 TextView로 변경
    private TextView textQuantity;     // EditText에서 TextView로 변경
    private TextView textPrice;       // EditText에서 TextView로 변경
    private Button btnApproval;
    private Button btnReject;

    private PendingItem pendingItem;
    private PendingCallback callback;

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

        // TextView로 캐스팅 (EditText가 아님)
        textProductName = findViewById(R.id.edit_product_name);
        textQuantity = findViewById(R.id.edit_quantity);
        textPrice = findViewById(R.id.edit_price);
        btnApproval = findViewById(R.id.btn_my_approval);
        btnReject = findViewById(R.id.btn_my_reject);

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
        textPrice.setText(String.valueOf(item.getTotalPrice()));
    }

    public void setCallback(PendingCallback callback) {
        this.callback = callback;
    }
}