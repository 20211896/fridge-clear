package com.example.t3.ui.orderhistory;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.t3.model.OrderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderHistoryViewModel extends AndroidViewModel {

    private static final String PREFS_NAME = "order_history_prefs";
    private static final String KEY_ORDER_HISTORY = "order_history";

    private MutableLiveData<List<OrderItem>> orderHistory;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public OrderHistoryViewModel(Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        gson = new Gson();
        orderHistory = new MutableLiveData<>();
        loadOrderHistory();
    }

    public LiveData<List<OrderItem>> getOrderHistory() {
        return orderHistory;
    }

    private void loadOrderHistory() {
        // SharedPreferences에서 주문 내역 로드
        String orderHistoryJson = sharedPreferences.getString(KEY_ORDER_HISTORY, null);

        List<OrderItem> orders = new ArrayList<>();

        if (orderHistoryJson != null && !orderHistoryJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<OrderItem>>(){}.getType();
                orders = gson.fromJson(orderHistoryJson, listType);

                // null 체크
                if (orders == null) {
                    orders = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
                orders = new ArrayList<>();
            }
        }

        orderHistory.setValue(orders);
    }

    private void saveOrderHistory() {
        List<OrderItem> currentOrders = orderHistory.getValue();
        if (currentOrders != null) {
            try {
                String orderHistoryJson = gson.toJson(currentOrders);
                sharedPreferences.edit()
                        .putString(KEY_ORDER_HISTORY, orderHistoryJson)
                        .apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 새 주문 추가 메서드 (구매 완료 시 호출) - 상태 매개변수 추가
    public void addOrder(String orderId, int totalAmount, List<String> orderDetails, String orderStatus) {
        List<OrderItem> currentOrders = orderHistory.getValue();
        if (currentOrders == null) {
            currentOrders = new ArrayList<>();
        }

        OrderItem newOrder = new OrderItem(
                orderId,
                new Date(),
                totalAmount,
                orderStatus, // 상태를 매개변수로 받음
                orderDetails
        );

        currentOrders.add(0, newOrder); // 맨 앞에 추가 (최신 주문이 위로)
        orderHistory.setValue(currentOrders);

        // SharedPreferences에 저장
        saveOrderHistory();
    }

    // 기존 메서드도 유지 (하위 호환성)
    public void addOrder(String orderId, int totalAmount, List<String> orderDetails) {
        addOrder(orderId, totalAmount, orderDetails, "주문 완료");
    }

    // 주문 제거 메서드 (길게 누르기로 삭제 시 호출)
    public void removeOrder(int position) {
        List<OrderItem> currentOrders = orderHistory.getValue();
        if (currentOrders != null && position >= 0 && position < currentOrders.size()) {
            currentOrders.remove(position);
            orderHistory.setValue(currentOrders);

            // SharedPreferences에 저장
            saveOrderHistory();
        }
    }

    // 모든 주문 내역 삭제 (필요시 사용)
    public void clearAllOrders() {
        orderHistory.setValue(new ArrayList<>());
        sharedPreferences.edit()
                .remove(KEY_ORDER_HISTORY)
                .apply();
    }
}