package com.example.t3.ui.basket;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.t3.R;
import com.example.t3.model.BasketItem;
import com.example.t3.model.PendingItem;
import com.example.t3.ui.pending.PendingApprovalViewModel;
import com.example.t3.utils.CustomToast;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class PurchaseBottomSheetDialog extends BottomSheetDialogFragment {

    // BasketFragment에서 체크 상태를 전달받기 위한 인터페이스
    public interface SharedItemCheckCallback {
        boolean isSharedItemChecked(PendingItem item);
    }

    private SharedItemCheckCallback sharedItemCheckCallback;

    // BasketFragment에서 호출할 메서드
    public void setSharedItemCheckCallback(SharedItemCheckCallback callback) {
        this.sharedItemCheckCallback = callback;
    }

    private BasketViewModel basketViewModel;
    private PendingApprovalViewModel pendingViewModel;

    // UI 컴포넌트들
    private TextView myPurchaseAmount;
    private TextView sharedPurchaseAmount;
    private TextView totalAmount;
    private LinearLayout myPurchaseDetails;
    private LinearLayout sharedPurchaseDetails;
    private LinearLayout myPurchaseHeader;
    private LinearLayout sharedPurchaseHeader;
    private TextView myPurchaseArrow;
    private TextView sharedPurchaseArrow;
    private FrameLayout slidePaymentContainer;
    private LinearLayout slideButton;
    private TextView slideGuideText;
    private TextView paymentCompleteText;

    // 데이터
    private List<BasketItem> myItems = new ArrayList<>();
    private List<PendingItem> sharedItems = new ArrayList<>();
    private int myTotal = 0;
    private int sharedTotal = 0;

    // 슬라이드 관련
    private boolean isSliding = false;
    private boolean isPurchaseComplete = false;
    private float slideButtonStartX = 0f;
    private float slideContainerWidth = 0f;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // 다이얼로그가 완전히 펼쳐지도록 설정
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog d = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_dialog, container, false);

        // ViewModel 초기화
        basketViewModel = new ViewModelProvider(requireActivity()).get(BasketViewModel.class);
        pendingViewModel = new ViewModelProvider(requireActivity()).get(PendingApprovalViewModel.class);

        // UI 초기화
        initViews(view);

        // 데이터 로드 및 UI 업데이트
        loadPurchaseData();
        setupClickListeners();
        setupSlidePayment();

        return view;
    }

    private void initViews(View view) {
        myPurchaseAmount = view.findViewById(R.id.my_purchase_amount);
        sharedPurchaseAmount = view.findViewById(R.id.shared_purchase_amount);
        totalAmount = view.findViewById(R.id.total_amount);
        myPurchaseDetails = view.findViewById(R.id.my_purchase_details);
        sharedPurchaseDetails = view.findViewById(R.id.shared_purchase_details);
        myPurchaseHeader = view.findViewById(R.id.my_purchase_header);
        sharedPurchaseHeader = view.findViewById(R.id.shared_purchase_header);
        myPurchaseArrow = view.findViewById(R.id.my_purchase_arrow);
        sharedPurchaseArrow = view.findViewById(R.id.shared_purchase_arrow);
        slidePaymentContainer = view.findViewById(R.id.slide_payment_container);
        slideButton = view.findViewById(R.id.slide_button);
        slideGuideText = view.findViewById(R.id.slide_guide_text);
        paymentCompleteText = view.findViewById(R.id.payment_complete_text);
    }

    private void loadPurchaseData() {
        // 내 장바구니에서 체크된 아이템들 가져오기
        List<BasketItem> basketItems = basketViewModel.getMyBasketItems().getValue();
        myItems.clear();
        myTotal = 0;

        if (basketItems != null) {
            for (BasketItem item : basketItems) {
                if (item.isChecked()) {
                    myItems.add(item);
                    myTotal += item.getTotalPrice();
                }
            }
        }

        // 공동장바구니에서 승인된 아이템들 가져오기
        List<PendingItem> pendingItems = pendingViewModel.getPendingItems().getValue();
        sharedItems.clear();
        sharedTotal = 0;

        if (pendingItems != null) {
            for (PendingItem item : pendingItems) {
                if (item.isApproved() && isSharedItemChecked(item)) {
                    sharedItems.add(item);
                    sharedTotal += item.getTotalPrice() / 2; // 50% 할인
                }
            }
        }

        // 구매할 상품이 없으면 다이얼로그 닫기
        if (myItems.isEmpty() && sharedItems.isEmpty()) {
            CustomToast.show(requireContext(), "구매할 상품을 선택해주세요");
            dismiss();
            return;
        }

        updateUI();
    }

    // BasketFragment의 체크 상태 확인
    private boolean isSharedItemChecked(PendingItem item) {
        if (sharedItemCheckCallback != null) {
            return sharedItemCheckCallback.isSharedItemChecked(item);
        }
        // 콜백이 없으면 기본값 false (체크 안됨)
        return false;
    }

    private void updateUI() {
        // 내 구매 금액 업데이트
        myPurchaseAmount.setText(String.format("%,d원", myTotal));

        // 공동구매 금액 업데이트
        sharedPurchaseAmount.setText(String.format("%,d원", sharedTotal));

        // 총 금액 업데이트
        int total = myTotal + sharedTotal;
        totalAmount.setText(String.format("%,d원", total));

        // 상세 내역 업데이트
        updateMyPurchaseDetails();
        updateSharedPurchaseDetails();
    }

    private void updateMyPurchaseDetails() {
        myPurchaseDetails.removeAllViews();

        if (!myItems.isEmpty()) {
            // 제목 추가
            TextView titleView = new TextView(requireContext());
            titleView.setText("구매 내역");
            titleView.setTextSize(14);
            titleView.setTextColor(getResources().getColor(android.R.color.black, null));
            titleView.setPadding(0, 0, 0, 24);
            myPurchaseDetails.addView(titleView);

            // 각 아이템 추가
            for (BasketItem item : myItems) {
                LinearLayout itemLayout = new LinearLayout(requireContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(0, 0, 0, 12);

                TextView nameView = new TextView(requireContext());
                nameView.setText(item.getProductName() + " " + item.getQuantity() + "개");
                nameView.setTextSize(14);
                nameView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                nameView.setLayoutParams(nameParams);

                TextView priceView = new TextView(requireContext());
                priceView.setText(String.format("%,d원", item.getTotalPrice()));
                priceView.setTextSize(14);
                priceView.setTextColor(getResources().getColor(android.R.color.black, null));

                itemLayout.addView(nameView);
                itemLayout.addView(priceView);
                myPurchaseDetails.addView(itemLayout);
            }
        }
    }

    private void updateSharedPurchaseDetails() {
        sharedPurchaseDetails.removeAllViews();

        if (!sharedItems.isEmpty()) {
            // 제목 추가
            TextView titleView = new TextView(requireContext());
            titleView.setText("공동구매 내역 (50% 할인)");
            titleView.setTextSize(14);
            titleView.setTextColor(getResources().getColor(android.R.color.black, null));
            titleView.setPadding(0, 0, 0, 24);
            sharedPurchaseDetails.addView(titleView);

            // 각 아이템 추가
            for (PendingItem item : sharedItems) {
                LinearLayout itemLayout = new LinearLayout(requireContext());
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(0, 0, 0, 12);

                TextView nameView = new TextView(requireContext());
                nameView.setText(item.getProductName() + " " + item.getQuantity() + "개");
                nameView.setTextSize(14);
                nameView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                nameView.setLayoutParams(nameParams);

                // 원래 가격 (취소선)
                TextView originalPriceView = new TextView(requireContext());
                originalPriceView.setText(String.format("%,d원", item.getTotalPrice()));
                originalPriceView.setTextSize(12);
                originalPriceView.setTextColor(getResources().getColor(android.R.color.darker_gray, null));
                originalPriceView.setPaintFlags(originalPriceView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                originalPriceView.setPadding(0, 0, 24, 0);

                // 할인 가격
                TextView discountPriceView = new TextView(requireContext());
                discountPriceView.setText(String.format("%,d원", item.getTotalPrice() / 2));
                discountPriceView.setTextSize(14);
                discountPriceView.setTextColor(getResources().getColor(android.R.color.holo_orange_dark, null));
                discountPriceView.setTypeface(null, android.graphics.Typeface.BOLD);

                itemLayout.addView(nameView);
                itemLayout.addView(originalPriceView);
                itemLayout.addView(discountPriceView);
                sharedPurchaseDetails.addView(itemLayout);
            }
        }
    }

    private void setupClickListeners() {
        // 내 구매 헤더 클릭 리스너
        myPurchaseHeader.setOnClickListener(v -> {
            if (myPurchaseDetails.getVisibility() == View.VISIBLE) {
                myPurchaseDetails.setVisibility(View.GONE);
                myPurchaseArrow.setText("▼");
            } else {
                myPurchaseDetails.setVisibility(View.VISIBLE);
                myPurchaseArrow.setText("▲");
            }
        });

        // 공동구매 헤더 클릭 리스너
        sharedPurchaseHeader.setOnClickListener(v -> {
            if (sharedPurchaseDetails.getVisibility() == View.VISIBLE) {
                sharedPurchaseDetails.setVisibility(View.GONE);
                sharedPurchaseArrow.setText("▼");
            } else {
                sharedPurchaseDetails.setVisibility(View.VISIBLE);
                sharedPurchaseArrow.setText("▲");
            }
        });
    }

    private void setupSlidePayment() {
        slideButton.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float buttonStartX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isPurchaseComplete) return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        buttonStartX = slideButton.getX();
                        slideButtonStartX = buttonStartX;
                        slideContainerWidth = slidePaymentContainer.getWidth() - slideButton.getWidth();
                        isSliding = true;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (isSliding) {
                            float currentX = event.getRawX();
                            float deltaX = currentX - startX;
                            float newX = buttonStartX + deltaX;

                            // 버튼이 컨테이너를 벗어나지 않도록 제한
                            if (newX < 0) newX = 0;
                            if (newX > slideContainerWidth) newX = slideContainerWidth;

                            slideButton.setX(newX);

                            // 가이드 텍스트 투명도 조절
                            float progress = newX / slideContainerWidth;
                            slideGuideText.setAlpha(1f - progress);

                            return true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isSliding) {
                            float currentX = slideButton.getX();
                            float progress = currentX / slideContainerWidth;

                            if (progress > 0.8f) { // 80% 이상 밀었을 때 결제 완료
                                completePurchase();
                            } else {
                                // 버튼을 원래 위치로 되돌리기
                                animateButtonToStart();
                            }

                            isSliding = false;
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void animateButtonToStart() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(slideButton, "x", slideButton.getX(), slideButtonStartX);
        animator.setDuration(200);
        animator.start();

        // 가이드 텍스트 투명도 복원
        ObjectAnimator textAnimator = ObjectAnimator.ofFloat(slideGuideText, "alpha", slideGuideText.getAlpha(), 1f);
        textAnimator.setDuration(200);
        textAnimator.start();
    }

    private void completePurchase() {
        isPurchaseComplete = true;

        // 버튼을 끝까지 이동
        ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(slideButton, "x", slideButton.getX(), slideContainerWidth);
        slideAnimator.setDuration(200);

        // 가이드 텍스트 숨기고 완료 텍스트 표시
        ObjectAnimator hideGuideAnimator = ObjectAnimator.ofFloat(slideGuideText, "alpha", 1f, 0f);
        hideGuideAnimator.setDuration(200);

        ObjectAnimator showCompleteAnimator = ObjectAnimator.ofFloat(paymentCompleteText, "alpha", 0f, 1f);
        showCompleteAnimator.setDuration(200);
        showCompleteAnimator.setStartDelay(200);

        // 애니메이션 시작
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideAnimator, hideGuideAnimator);
        animatorSet.start();

        // 완료 텍스트 표시
        paymentCompleteText.setVisibility(View.VISIBLE);
        showCompleteAnimator.start();

        // 1.5초 후 다이얼로그 닫고 장바구니 업데이트
        slidePaymentContainer.postDelayed(() -> {
            processPurchaseCompletion();
            dismiss();
        }, 1500);
    }

    private void processPurchaseCompletion() {
        // 구매한 내 장바구니 아이템들 제거
        for (BasketItem item : myItems) {
            basketViewModel.removeItem(item);
        }

        // 구매한 공동장바구니 아이템들 제거 (승인대기는 유지)
        for (PendingItem item : sharedItems) {
            pendingViewModel.removePendingItem(item);
        }

        CustomToast.show(requireContext(), "입금이 완료되었습니다!");
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}