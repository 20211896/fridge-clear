package com.example.t3.ui.orderhistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t3.databinding.FragmentOrderHistoryBinding;

public class OrderHistoryFragment extends Fragment {

    private FragmentOrderHistoryBinding binding;
    private OrderHistoryViewModel orderHistoryViewModel;
    private OrderHistoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Activity 범위의 AndroidViewModel 사용 (Application Context 필요)
        orderHistoryViewModel = new ViewModelProvider(requireActivity()).get(OrderHistoryViewModel.class);

        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // RecyclerView 설정
        setupRecyclerView();

        // ViewModel 관찰
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewOrderHistory;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrderHistoryAdapter();

        // 주문 제거 리스너 설정
        adapter.setOnOrderRemoveListener((position, orderItem) -> {
            // 확인 다이얼로그 표시
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("주문 삭제")
                    .setMessage("주문번호 " + orderItem.getOrderId() + "을(를) 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        // ViewModel에서 주문 제거
                        orderHistoryViewModel.removeOrder(position);
                        // 어댑터에서 아이템 제거
                        adapter.removeOrder(position);
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        orderHistoryViewModel.getOrderHistory().observe(getViewLifecycleOwner(), orderList -> {
            if (orderList != null && !orderList.isEmpty()) {
                adapter.updateOrderList(orderList);
                binding.textEmptyHistory.setVisibility(View.GONE);
                binding.recyclerViewOrderHistory.setVisibility(View.VISIBLE);
            } else {
                binding.textEmptyHistory.setVisibility(View.VISIBLE);
                binding.recyclerViewOrderHistory.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}