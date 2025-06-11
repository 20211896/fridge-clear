package com.example.t3.ui.basket;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.t3.R;
import com.example.t3.model.BasketItem;
import com.example.t3.model.PendingItem;
import com.example.t3.ui.pending.PendingApprovalViewModel;
import com.example.t3.ui.pending.PendingItemView;
import com.example.t3.utils.CustomToast;

import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment {

    public static interface ParentCallback {
        void onItemRemoved(BasketItem removed);
        void onQuantityChanged(BasketItem updated, int newQty);
    }

    private BasketViewModel basketViewModel;
    private PendingApprovalViewModel pendingViewModel;
    private LinearLayout layoutMyItems;
    private LinearLayout layoutPendingItems, layoutPendingItemsUser;

    // 가격 표시용 TextView들
    private TextView myTotalCount, myTotalPrice;
    private TextView userTotalCount, userTotalPrice;

    // 탭 관련 뷰들
    private TextView tabBom, tabSeoul;
    private View tabIndicator1, tabIndicator2;
    private TextView myTab2Approval, myTab2Shared;
    private View myTab2Indicator1, myTab2Indicator2;
    private TextView userTab2Approval, userTab2Shared;
    private View userTab2Indicator1, userTab2Indicator2;
    private LinearLayout myTabContainer, userTabContainer;
    private LinearLayout bottomSectionMy, bottomSectionUser;
    private ScrollView scrollViewPending;
    private LinearLayout sharedBottomContent;

    // 현재 선택된 탭 상태
    private boolean isMyTab = true; // true: 나, false: 사용자
    private boolean isApprovalTab = true; // true: 승인대기, false: 공동장바구니

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_basket, container, false);

        // 뷰 바인딩
        initViews(root);

        // ViewModel 초기화
        basketViewModel = new ViewModelProvider(requireActivity())
                .get(BasketViewModel.class);
        pendingViewModel = new ViewModelProvider(requireActivity())
                .get(PendingApprovalViewModel.class);

        // 탭 클릭 리스너 설정
        setupTabListeners();

        // LiveData 관찰
        basketViewModel.getMyBasketItems()
                .observe(getViewLifecycleOwner(), this::populateMyItems);

        pendingViewModel.getPendingItems()
                .observe(getViewLifecycleOwner(), this::handlePendingItemsUpdate);

        // 가격 업데이트를 위한 추가 관찰
        pendingViewModel.getPendingItems()
                .observe(getViewLifecycleOwner(), items -> updateTotalPrices());
        basketViewModel.getMyBasketItems()
                .observe(getViewLifecycleOwner(), items -> updateTotalPrices());

        // 승인대기 영역에 드롭 존 설정 (나 탭)
        setupDropZone(bottomSectionMy);
        // 사용자 탭에도 동일한 드롭존 설정
        setupDropZone(bottomSectionUser);

        return root;
    }

    private void initViews(View root) {
        layoutMyItems = root.findViewById(R.id.layout_my_items);
        layoutPendingItems = root.findViewById(R.id.layout_pending_items);
        layoutPendingItemsUser = root.findViewById(R.id.layout_pending_items_user);

        // 상단 탭
        tabBom = root.findViewById(R.id.tab_bom);
        tabSeoul = root.findViewById(R.id.tab_seoul);
        tabIndicator1 = root.findViewById(R.id.tab_indicator1);
        tabIndicator2 = root.findViewById(R.id.tab_indicator2);

        // 나 탭의 하단 탭
        myTab2Approval = root.findViewById(R.id.my_tab2_approval);
        myTab2Shared = root.findViewById(R.id.my_tab2_shared);
        myTab2Indicator1 = root.findViewById(R.id.my_tab2_indicator1);
        myTab2Indicator2 = root.findViewById(R.id.my_tab2_indicator2);

        // 사용자 탭의 하단 탭
        userTab2Approval = root.findViewById(R.id.user_tab2_approval);
        userTab2Shared = root.findViewById(R.id.user_tab2_shared);
        userTab2Indicator1 = root.findViewById(R.id.user_tab2_indicator1);
        userTab2Indicator2 = root.findViewById(R.id.user_tab2_indicator2);

        // 컨테이너
        myTabContainer = root.findViewById(R.id.my_tab_container);
        userTabContainer = root.findViewById(R.id.user_tab_container);
        bottomSectionMy = root.findViewById(R.id.bottom_section_my);
        bottomSectionUser = root.findViewById(R.id.bottom_section_user);

        // 가격 표시용 TextView들
        myTotalCount = root.findViewById(R.id.my_total_count);
        myTotalPrice = root.findViewById(R.id.my_total_price);
        userTotalCount = root.findViewById(R.id.user_total_count);
        userTotalPrice = root.findViewById(R.id.user_total_price);

        // 공유되는 하단 영역 (초기에는 나 탭이 보임)
        scrollViewPending = root.findViewById(R.id.scrollView_pending);
        sharedBottomContent = bottomSectionMy;
    }

    private void setupTabListeners() {
        // 상단 탭 클릭 리스너
        tabBom.setOnClickListener(v -> switchToMyTab());
        tabSeoul.setOnClickListener(v -> switchToUserTab());

        // 나 탭의 하단 탭 클릭 리스너
        myTab2Approval.setOnClickListener(v -> switchToApprovalTab());
        myTab2Shared.setOnClickListener(v -> switchToSharedTab());

        // 사용자 탭의 하단 탭 클릭 리스너 (같은 기능)
        userTab2Approval.setOnClickListener(v -> switchToApprovalTab());
        userTab2Shared.setOnClickListener(v -> switchToSharedTab());
    }

    private void switchToMyTab() {
        if (!isMyTab) {
            isMyTab = true;
            updateTopTabUI();
            myTabContainer.setVisibility(View.VISIBLE);
            userTabContainer.setVisibility(View.GONE);
            moveBottomSectionToContainer(bottomSectionMy);
            // 토스트 제거
        }
    }

    /**
     * 총 가격 계산 및 업데이트
     */
    private void updateTotalPrices() {
        // 내 장바구니 체크된 아이템들의 가격 계산
        int myItemCount = 0;
        int myTotalAmount = 0;
        List<BasketItem> basketItems = basketViewModel.getMyBasketItems().getValue();
        if (basketItems != null) {
            for (BasketItem item : basketItems) {
                if (item.isChecked()) {
                    myItemCount++;
                    myTotalAmount += item.getTotalPrice();
                }
            }
        }

        // 공동장바구니 체크된 아이템들의 가격 계산 (절반 가격)
        int sharedItemCount = 0;
        int sharedTotalAmount = 0;
        List<PendingItem> pendingItems = pendingViewModel.getPendingItems().getValue();
        if (pendingItems != null) {
            for (PendingItem item : pendingItems) {
                if (item.isApproved()) {
                    // 공동장바구니 아이템은 체크박스 상태를 확인해야 함
                    if (isSharedItemChecked(item)) {
                        sharedItemCount++;
                        sharedTotalAmount += item.getTotalPrice() / 2; // 절반 가격
                    }
                }
            }
        }

        // 최종 가격 계산
        int finalTotalAmount = myTotalAmount + sharedTotalAmount;

        // UI 업데이트
        String countText = String.format("내 상품 %d건, 공동상품 %d건", myItemCount, sharedItemCount);
        String priceText = String.format("%,d", finalTotalAmount);

        // 나 탭과 사용자 탭 모두 업데이트
        myTotalCount.setText(countText);
        myTotalPrice.setText(priceText);
        userTotalCount.setText(countText);
        userTotalPrice.setText(priceText);
    }

    /**
     * 공동장바구니 아이템의 체크 상태 확인
     */
    private boolean isSharedItemChecked(PendingItem item) {
        // 현재 표시되고 있는 공동장바구니 레이아웃에서 해당 아이템의 체크박스 상태 확인
        LinearLayout activeLayout = isMyTab ? layoutPendingItems : layoutPendingItemsUser;

        if (!isApprovalTab && activeLayout != null) { // 공동장바구니 탭인 경우에만
            for (int i = 0; i < activeLayout.getChildCount(); i++) {
                View childView = activeLayout.getChildAt(i);
                CheckBox checkbox = childView.findViewById(R.id.checkbox_item);
                TextView productName = childView.findViewById(R.id.edit_product_name);

                if (checkbox != null && productName != null &&
                        productName.getText().toString().equals(item.getProductName())) {
                    return checkbox.isChecked();
                }
            }
        }
        return true; // 기본값은 체크됨
    }

    private void switchToUserTab() {
        if (isMyTab) {
            isMyTab = false;
            updateTopTabUI();
            myTabContainer.setVisibility(View.GONE);
            userTabContainer.setVisibility(View.VISIBLE);
            moveBottomSectionToContainer(bottomSectionUser);
            // 토스트 제거
        }
    }

    private void switchToApprovalTab() {
        if (!isApprovalTab) {
            isApprovalTab = true;
            updateBottomTabUI();
            updateBottomContent();
            updateTotalPrices(); // 탭 전환 시 가격 업데이트
            // 토스트 제거
        }
    }

    private void switchToSharedTab() {
        if (isApprovalTab) {
            isApprovalTab = false;
            updateBottomTabUI();
            updateBottomContent();
            updateTotalPrices(); // 탭 전환 시 가격 업데이트
            // 토스트 제거
        }
    }

    /**
     * 하단 탭 상태에 따라 콘텐츠 업데이트
     */
    private void updateBottomContent() {
        List<PendingItem> allItems = pendingViewModel.getPendingItems().getValue();
        if (allItems == null) allItems = new ArrayList<>();

        if (isApprovalTab) {
            // 승인대기 콘텐츠 표시 (PENDING 상태만)
            List<PendingItem> pendingItems = new ArrayList<>();
            for (PendingItem item : allItems) {
                if (item.isPending()) {
                    pendingItems.add(item);
                }
            }
            populatePendingItems(pendingItems);
        } else {
            // 공동장바구니 콘텐츠 표시 (APPROVED 상태만)
            List<PendingItem> approvedItems = new ArrayList<>();
            for (PendingItem item : allItems) {
                if (item.isApproved()) {
                    approvedItems.add(item);
                }
            }
            populateSharedItems(approvedItems);
        }
    }

    /**
     * PendingItems 업데이트 처리 - 현재 탭에 따라 필터링
     */
    private void handlePendingItemsUpdate(List<PendingItem> allItems) {
        updateBottomContent();
    }

    private void updateTopTabUI() {
        if (isMyTab) {
            // 나 탭 활성화
            tabBom.setTextColor(getResources().getColor(R.color.green, null));
            tabBom.setTextSize(16);
            tabBom.getPaint().setFakeBoldText(true);

            tabSeoul.setTextColor(getResources().getColor(R.color.gray_666, null));
            tabSeoul.setTextSize(16);
            tabSeoul.getPaint().setFakeBoldText(false);

            tabIndicator1.setBackgroundColor(getResources().getColor(R.color.green, null));
            tabIndicator2.setBackgroundColor(getResources().getColor(R.color.gray_e0, null));
        } else {
            // 사용자 탭 활성화
            tabBom.setTextColor(getResources().getColor(R.color.gray_666, null));
            tabBom.setTextSize(16);
            tabBom.getPaint().setFakeBoldText(false);

            tabSeoul.setTextColor(getResources().getColor(R.color.green, null));
            tabSeoul.setTextSize(16);
            tabSeoul.getPaint().setFakeBoldText(true);

            tabIndicator1.setBackgroundColor(getResources().getColor(R.color.gray_e0, null));
            tabIndicator2.setBackgroundColor(getResources().getColor(R.color.green, null));
        }
    }

    private void updateBottomTabUI() {
        // 나 탭의 하단 탭 UI 업데이트
        updateTabUI(myTab2Approval, myTab2Shared, myTab2Indicator1, myTab2Indicator2, isApprovalTab);

        // 사용자 탭의 하단 탭 UI 업데이트 (동일하게)
        updateTabUI(userTab2Approval, userTab2Shared, userTab2Indicator1, userTab2Indicator2, isApprovalTab);
    }

    private void updateTabUI(TextView tab1, TextView tab2, View indicator1, View indicator2, boolean isFirstTabActive) {
        if (isFirstTabActive) {
            // 첫 번째 탭 활성화
            tab1.setTextColor(getResources().getColor(R.color.green, null));
            tab1.getPaint().setFakeBoldText(true);

            tab2.setTextColor(getResources().getColor(R.color.gray_666, null));
            tab2.getPaint().setFakeBoldText(false);

            indicator1.setBackgroundColor(getResources().getColor(R.color.green, null));
            indicator2.setBackgroundColor(getResources().getColor(R.color.gray_e0, null));
        } else {
            // 두 번째 탭 활성화
            tab1.setTextColor(getResources().getColor(R.color.gray_666, null));
            tab1.getPaint().setFakeBoldText(false);

            tab2.setTextColor(getResources().getColor(R.color.green, null));
            tab2.getPaint().setFakeBoldText(true);

            indicator1.setBackgroundColor(getResources().getColor(R.color.gray_e0, null));
            indicator2.setBackgroundColor(getResources().getColor(R.color.green, null));
        }
    }

    /**
     * 하단 영역(승인대기/공동장바구니)을 현재 활성화된 탭의 컨테이너로 이동
     */
    private void moveBottomSectionToContainer(LinearLayout targetContainer) {
        // 이미 목표 컨테이너에 있다면 아무것도 하지 않음
        if (sharedBottomContent == targetContainer) {
            return;
        }

        // 간단한 방법: visibility 조정으로 처리
        bottomSectionMy.setVisibility(targetContainer == bottomSectionMy ? View.VISIBLE : View.GONE);
        bottomSectionUser.setVisibility(targetContainer == bottomSectionUser ? View.VISIBLE : View.GONE);

        // 현재 공유 컨테이너 업데이트
        sharedBottomContent = targetContainer;
    }

    private void setupDropZone(View dropZone) {
        dropZone.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        // 드래그 진입 시 배경색 변경으로 시각적 피드백
                        v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light, null));
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        // 드래그 벗어남 시 원래 색상 복원
                        v.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                        return true;

                    case DragEvent.ACTION_DROP:
                        // 드롭 시 원래 색상 복원
                        v.setBackgroundColor(getResources().getColor(android.R.color.white, null));

                        // 드래그된 뷰에서 BasketItem 추출
                        View draggedView = (View) event.getLocalState();
                        if (draggedView instanceof BasketItemView) {
                            BasketItemView basketItemView = (BasketItemView) draggedView;
                            BasketItem basketItem = basketItemView.getBasketItem();

                            // BasketItem을 PendingItem으로 변환
                            PendingItem pendingItem = new PendingItem(
                                    basketItem.getId(),
                                    basketItem.getProductName(),
                                    basketItem.getQuantity(),
                                    basketItem.getUnitPrice(),
                                    basketItem.getImageUrl()
                            );

                            // 승인대기에 추가
                            pendingViewModel.addPendingItem(pendingItem);

                            // 장바구니에서 제거
                            basketViewModel.removeItem(basketItem);

                            CustomToast.show(getContext(),
                                    basketItem.getProductName() + " 승인대기 추가");
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // 드래그 종료 시 원래 색상 복원
                        v.setBackgroundColor(getResources().getColor(android.R.color.white, null));
                        return true;
                }
                return false;
            }
        });
    }

    private void populateMyItems(List<BasketItem> items) {
        layoutMyItems.removeAllViews();
        for (BasketItem item : items) {
            BasketItemView itemView = new BasketItemView(requireContext());
            itemView.bind(item);

            // 콜백 연결 (삭제, 수량 변경)
            itemView.setParentCallback(new ParentCallback() {
                @Override
                public void onItemRemoved(BasketItem removed) {
                    basketViewModel.removeItem(removed);
                }
                @Override
                public void onQuantityChanged(BasketItem updated, int newQty) {
                    basketViewModel.updateItemQuantity(updated, newQty);
                }
            });

            // 체크박스 상태 변경 시 가격 업데이트
            View checkboxView = itemView.findViewById(R.id.checkbox_item);
            if (checkboxView instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) checkboxView;
                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    item.setChecked(isChecked);
                    updateTotalPrices(); // 가격 즉시 업데이트
                });
            }

            layoutMyItems.addView(itemView);
        }
    }

    private void populatePendingItems(List<PendingItem> items) {
        // 나 탭의 pending 레이아웃 업데이트
        layoutPendingItems.removeAllViews();
        populatePendingItemsInLayout(items, layoutPendingItems);

        // 사용자 탭의 pending 레이아웃도 동일하게 업데이트
        layoutPendingItemsUser.removeAllViews();
        populatePendingItemsInLayout(items, layoutPendingItemsUser);
    }

    /**
     * 공동장바구니 아이템들을 basket_bottom_item2 레이아웃으로 표시
     */
    private void populateSharedItems(List<PendingItem> approvedItems) {
        // 나 탭의 레이아웃 업데이트
        layoutPendingItems.removeAllViews();
        populateSharedItemsInLayout(approvedItems, layoutPendingItems);

        // 사용자 탭의 레이아웃도 동일하게 업데이트
        layoutPendingItemsUser.removeAllViews();
        populateSharedItemsInLayout(approvedItems, layoutPendingItemsUser);
    }

    private void populatePendingItemsInLayout(List<PendingItem> items, LinearLayout targetLayout) {
        for (PendingItem item : items) {
            PendingItemView itemView = new PendingItemView(requireContext());
            itemView.bind(item);

            // 승인/거부 콜백 설정
            itemView.setCallback(new PendingItemView.PendingCallback() {
                @Override
                public void onApproval(PendingItem approvedItem) {
                    // 승인된 아이템의 상태를 APPROVED로 변경
                    approvedItem.setStatus(PendingItem.Status.APPROVED);
                    pendingViewModel.updatePendingItem(approvedItem);

                    CustomToast.show(getContext(),
                            approvedItem.getProductName() + " 공동장바구니 이동");
                }

                @Override
                public void onReject(PendingItem rejectedItem) {
                    // 거부 시 다시 장바구니로 돌리기
                    BasketItem basketItem = new BasketItem(
                            rejectedItem.getId(),
                            rejectedItem.getProductName(),
                            rejectedItem.getUnitPrice(),
                            rejectedItem.getQuantity(),
                            rejectedItem.getImageUrl()
                    );
                    basketViewModel.addItem(basketItem);
                    pendingViewModel.removePendingItem(rejectedItem);

                    CustomToast.show(getContext(),
                            rejectedItem.getProductName() + " 장바구니 복귀");
                }
            });

            targetLayout.addView(itemView);
        }
    }

    /**
     * 공동장바구니 아이템들을 basket_bottom_item2 레이아웃으로 표시
     */
    private void populateSharedItemsInLayout(List<PendingItem> approvedItems, LinearLayout targetLayout) {
        for (PendingItem item : approvedItems) {
            // basket_bottom_item2 레이아웃 사용
            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.basket_bottom_item2_layout, targetLayout, false);

            // 뷰 바인딩
            CheckBox checkboxItem = itemView.findViewById(R.id.checkbox_item);
            TextView editProductName = itemView.findViewById(R.id.edit_product_name);
            TextView editQuantity = itemView.findViewById(R.id.edit_quantity);
            TextView editPrice = itemView.findViewById(R.id.edit_price);
            Button btnMyReject = itemView.findViewById(R.id.btn_my_reject);

            // 데이터 바인딩
            checkboxItem.setChecked(true);
            editProductName.setText(item.getProductName());
            editQuantity.setText(String.valueOf(item.getQuantity()));
            editPrice.setText(item.getFormattedPrice());

            // 체크박스 상태 변경 시 가격 업데이트
            checkboxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTotalPrices(); // 가격 즉시 업데이트
            });

            // 취소 버튼 리스너
            btnMyReject.setOnClickListener(v -> {
                // 공동장바구니에서 승인대기로 상태 변경
                item.setStatus(PendingItem.Status.PENDING);
                pendingViewModel.updatePendingItem(item);
                CustomToast.show(getContext(),
                        item.getProductName() + " 승인대기 이동");
            });

            targetLayout.addView(itemView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Fragment가 다시 보일 때마다 장바구니 새로고침
        basketViewModel.refreshItems();
        pendingViewModel.refreshItems(); // PendingManager에서 데이터 다시 로드

        // 초기 탭 UI 설정
        updateTopTabUI();
        updateBottomTabUI();
        updateBottomContent();
        updateTotalPrices(); // 가격 업데이트 추가

        // 초기 하단 영역 위치 설정
        if (isMyTab) {
            moveBottomSectionToContainer(bottomSectionMy);
        } else {
            moveBottomSectionToContainer(bottomSectionUser);
        }
    }
}