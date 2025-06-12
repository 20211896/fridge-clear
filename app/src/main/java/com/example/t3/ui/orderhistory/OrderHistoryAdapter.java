package com.example.t3.ui.orderhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t3.R;
import com.example.t3.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<OrderItem> orderList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
    private OnOrderRemoveListener onOrderRemoveListener;

    // 주문 제거 리스너 인터페이스
    public interface OnOrderRemoveListener {
        void onOrderRemove(int position, OrderItem orderItem);
    }

    public void setOnOrderRemoveListener(OnOrderRemoveListener listener) {
        this.onOrderRemoveListener = listener;
    }

    public void updateOrderList(List<OrderItem> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    public void removeOrder(int position) {
        if (position >= 0 && position < orderList.size()) {
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textOrderId;
        private TextView textOrderDate;
        private TextView textOrderStatus;
        private TextView textTotalAmount;
        private LinearLayout layoutOrderDetails;
        private TextView textToggleDetails;
        private boolean isDetailsVisible = false;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.text_order_id);
            textOrderDate = itemView.findViewById(R.id.text_order_date);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            textTotalAmount = itemView.findViewById(R.id.text_total_amount);
            layoutOrderDetails = itemView.findViewById(R.id.layout_order_details);
            textToggleDetails = itemView.findViewById(R.id.text_toggle_details);

            // 상세보기 토글 클릭 리스너
            textToggleDetails.setOnClickListener(v -> toggleDetails());
            itemView.setOnClickListener(v -> toggleDetails());

            // 길게 누르면 삭제하는 리스너 추가
            itemView.setOnLongClickListener(v -> {
                if (onOrderRemoveListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        OrderItem orderItem = orderList.get(position);
                        onOrderRemoveListener.onOrderRemove(position, orderItem);
                    }
                }
                return true;
            });
        }

        public void bind(OrderItem order) {
            textOrderId.setText("주문번호: " + order.getOrderId());
            textOrderDate.setText(dateFormat.format(order.getOrderDate()));
            textOrderStatus.setText(order.getStatus());
            textTotalAmount.setText(String.format("%,d원", order.getTotalAmount()));

            // 상태에 따른 색상 설정
            switch (order.getStatus()) {
                case "주문 완료":
                case "배송 완료":
                    textOrderStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark, null));
                    break;
                case "승인중":
                    textOrderStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark, null));
                    break;
                case "배송 중":
                    textOrderStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark, null));
                    break;
                case "주문 취소":
                    textOrderStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark, null));
                    break;
                default:
                    textOrderStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black, null));
                    break;
            }

            // 주문 상세 내역 추가
            layoutOrderDetails.removeAllViews();
            for (String detail : order.getOrderDetails()) {
                TextView detailView = new TextView(itemView.getContext());
                detailView.setText("• " + detail);
                detailView.setTextSize(14);
                detailView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray, null));
                detailView.setPadding(0, 4, 0, 4);
                layoutOrderDetails.addView(detailView);
            }

            // 초기 상태 설정
            layoutOrderDetails.setVisibility(View.GONE);
            textToggleDetails.setText("상세보기 ▼");
            isDetailsVisible = false;
        }

        private void toggleDetails() {
            if (isDetailsVisible) {
                layoutOrderDetails.setVisibility(View.GONE);
                textToggleDetails.setText("상세보기 ▼");
                isDetailsVisible = false;
            } else {
                layoutOrderDetails.setVisibility(View.VISIBLE);
                textToggleDetails.setText("접기 ▲");
                isDetailsVisible = true;
            }
        }
    }
}