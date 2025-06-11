package com.example.t3.ui.market;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.t3.R;
import com.example.t3.adapter.KamisProductAdapter;
import com.example.t3.databinding.FragmentMarketBinding;
import com.example.t3.manager.BasketManager;
import com.example.t3.model.BasketItem;
import com.example.t3.model.KamisProduct;
import com.example.t3.ui.basket.BasketViewModel;
import com.example.t3.utils.CustomToast;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MarketFragment extends Fragment implements KamisProductAdapter.OnKamisProductClickListener {

    private FragmentMarketBinding binding;
    private MarketViewModel marketViewModel;
    private BasketViewModel basketViewModel;
    private BasketManager basketManager; // 🎯 BasketManager 추가
    private KamisProductAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMarketBinding.inflate(inflater, container, false);

        // ViewModel 초기화
        marketViewModel = new ViewModelProvider(this).get(MarketViewModel.class);
        // Activity 스코프로 공유되는 BasketViewModel
        basketViewModel = new ViewModelProvider(requireActivity()).get(BasketViewModel.class);

        // 🎯 BasketManager 초기화
        basketManager = BasketManager.getInstance(requireContext());

        setupRecyclerView();
        setupSearchView();
        setupCategorySpinner();
        observeMarketData();

        return binding.getRoot();
    }

    /** 1) RecyclerView 세팅 **/
    private void setupRecyclerView() {
        adapter = new KamisProductAdapter();
        adapter.setOnProductClickListener(this);
        binding.recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerProducts.setAdapter(adapter);
    }

    /** 2) 검색창 & 검색 버튼 동작 세팅 **/
    private void setupSearchView() {
        binding.editSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                marketViewModel.searchProducts(s.toString().trim());
            }
        });

        binding.btnSearch.setOnClickListener(v -> {
            String q = binding.editSearchProducts.getText().toString().trim();
            marketViewModel.searchProducts(q);
            if (!q.isEmpty()) {
                CustomToast.show(getContext(), q + " 검색중");
            }
        });
    }

    /** 3) 카테고리 스피너 동작 세팅 **/
    private void setupCategorySpinner() {
        List<String> categories = marketViewModel.getCategories();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(spinnerAdapter);

        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                marketViewModel.filterByCategory(categories.get(pos));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /** 4) ViewModel LiveData 관찰해서 UI 갱신 **/
    private void observeMarketData() {
        // 로딩 상태
        marketViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.layoutSearch.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            binding.textMarket.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerProducts.setVisibility(View.GONE);
            if (isLoading) {
                binding.textMarket.setText("농산물 정보를 불러오는 중...");
            }
        });

        // 상품 목록
        marketViewModel.getKamisProducts().observe(getViewLifecycleOwner(), products -> {
            // 검색/필터 영역 항상 보이기
            binding.layoutSearch.setVisibility(View.VISIBLE);

            if (products == null || products.isEmpty()) {
                binding.recyclerProducts.setVisibility(View.GONE);
                // 검색어에 따른 문구
                String q = binding.editSearchProducts.getText().toString().trim();
                if (!q.isEmpty()) {
                    binding.textMarket.setText("'" + q + "' 검색 결과가 없습니다.");
                } else {
                    binding.textMarket.setText("해당 카테고리에 상품이 없습니다.");
                }
                binding.textMarket.setVisibility(View.VISIBLE);
            } else {
                binding.textMarket.setVisibility(View.GONE);
                binding.recyclerProducts.setVisibility(View.VISIBLE);
                adapter.setProducts(products);
            }
        });

        // 에러 상태
        marketViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.layoutSearch.setVisibility(View.GONE);
                binding.recyclerProducts.setVisibility(View.GONE);
                binding.textMarket.setVisibility(View.VISIBLE);
                binding.textMarket.setText("❌ " + error);
                CustomToast.show(getContext(), "오류: " + error);
            }
        });
    }

    /** 5) "장바구니 담기" 클릭 리스너 **/
    @Override
    public void onAddToCartClick(KamisProduct product) {
        showQuantityDialog(product);
    }

    /** 6) 🎯 수량 선택 다이얼로그 & 이미지 URL 포함한 장바구니 추가 **/
    private void showQuantityDialog(KamisProduct product) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_quantity_selector, null);

        TextView txtName  = dialogView.findViewById(R.id.txt_dialog_product_name);
        TextView txtPrice = dialogView.findViewById(R.id.txt_dialog_product_price);
        TextView txtQty   = dialogView.findViewById(R.id.txt_dialog_quantity);
        TextView txtTotal = dialogView.findViewById(R.id.txt_dialog_total_price);
        Button btnMinus   = dialogView.findViewById(R.id.btn_dialog_minus);
        Button btnPlus    = dialogView.findViewById(R.id.btn_dialog_plus);
        Button btnCancel  = dialogView.findViewById(R.id.btn_dialog_cancel);
        Button btnAdd     = dialogView.findViewById(R.id.btn_dialog_add_to_cart);

        txtName.setText(product.getFullName());
        txtPrice.setText(product.getFormattedPrice());

        final int[] qty = {1};
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.KOREA);

        Runnable updateTotal = () -> {
            txtQty.setText(String.valueOf(qty[0]));
            txtTotal.setText("총 " +
                    fmt.format((int)(product.getPriceAsDouble() * qty[0])) + "원");
        };
        updateTotal.run();

        btnMinus.setOnClickListener(v -> {
            if (qty[0] > 1) { qty[0]--; updateTotal.run(); }
        });
        btnPlus.setOnClickListener(v -> {
            if (qty[0] < 99) { qty[0]++; updateTotal.run(); }
        });

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            // 🎯 이미지 URL을 포함한 BasketItem 생성
            String id = String.valueOf(System.currentTimeMillis());
            String name = product.getFullName();
            int unitPrice = (int) Math.round(product.getPriceAsDouble());
            String imageUrl = product.getImageUrl(); // 상품의 이미지 URL 가져오기

            BasketItem basketItem = new BasketItem(id, name, unitPrice, qty[0], imageUrl);

            // BasketManager에 추가
            basketManager.addMyBasketItem(basketItem);

            // 🎯 ViewModel도 업데이트 (실시간 반영)
            basketViewModel.refreshItems();

            // 상세한 성공 메시지 표시
            CustomToast.show(getContext(),
                    "🛒 " + name + "\n" +
                            "수량: " + qty[0] + "개\n" +
                            "가격: " + fmt.format(unitPrice * qty[0]) + "원\n" +
                            "장바구니에 추가되었습니다!");

            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}