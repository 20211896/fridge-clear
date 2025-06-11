package com.example.t3.ui.market;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.t3.data.KamisDataManager;
import com.example.t3.model.KamisProduct;

import java.util.ArrayList;
import java.util.List;

public class MarketViewModel extends ViewModel {

    private final MutableLiveData<List<KamisProduct>> kamisProducts;
    private final MutableLiveData<Boolean> loading;
    private final MutableLiveData<String> error;
    private final MutableLiveData<String> selectedCategory;
    private final MutableLiveData<String> searchQuery;

    private List<KamisProduct> allProducts;

    public MarketViewModel() {
        kamisProducts = new MutableLiveData<>();
        loading = new MutableLiveData<>();
        error = new MutableLiveData<>();
        selectedCategory = new MutableLiveData<>();
        searchQuery = new MutableLiveData<>();

        selectedCategory.setValue("전체");
        searchQuery.setValue("");
        loadKamisProducts();
    }

    public LiveData<List<KamisProduct>> getKamisProducts() {
        return kamisProducts;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    private void loadKamisProducts() {
        loading.setValue(true);

        // 실제 환경에서는 KAMIS API 호출
        new Thread(() -> {
            try {
                // 로딩 시뮬레이션
                Thread.sleep(1500);

                allProducts = KamisDataManager.getKamisProducts();

                // UI 스레드에서 업데이트
                loading.postValue(false);
                kamisProducts.postValue(allProducts);

            } catch (InterruptedException e) {
                loading.postValue(false);
                error.postValue("농산물 가격정보를 불러오는 중 오류가 발생했습니다.");
            }
        }).start();
    }

    public void filterByCategory(String category) {
        if (category.equals(selectedCategory.getValue())) {
            return; // 같은 카테고리면 무시
        }

        selectedCategory.setValue(category);
        applyFilters();
    }

    public void searchProducts(String query) {
        searchQuery.setValue(query);
        applyFilters();
    }

    private void applyFilters() {
        if (allProducts == null) return;

        String currentCategory = selectedCategory.getValue();
        String currentQuery = searchQuery.getValue();

        List<KamisProduct> filtered;

        // 카테고리와 검색어 모두 적용
        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            filtered = KamisDataManager.searchProductsByCategory(currentCategory, currentQuery);
        } else {
            filtered = KamisDataManager.getProductsByCategory(currentCategory);
        }

        kamisProducts.setValue(filtered);
    }

    public List<String> getCategories() {
        return KamisDataManager.getKamisCategories();
    }

    public void refreshProducts() {
        loadKamisProducts();
    }

    // 추천 검색어 제공
    public List<String> getPopularSearchKeywords() {
        return List.of("사과", "배추", "쇠고기", "고등어", "토마토", "당근", "돼지고기", "딸기", "감자", "새우");
    }

    // 카테고리별 상품 개수
    public int getProductCountByCategory(String category) {
        if (allProducts == null) return 0;
        return KamisDataManager.getProductsByCategory(category).size();
    }

    // 현재 필터링된 상품 개수
    public int getCurrentProductCount() {
        List<KamisProduct> current = kamisProducts.getValue();
        return current != null ? current.size() : 0;
    }

    // 검색 히스토리 관리 (나중에 SharedPreferences로 저장 가능)
    private List<String> searchHistory = new ArrayList<>();

    public void addToSearchHistory(String query) {
        if (query != null && !query.trim().isEmpty()) {
            searchHistory.remove(query); // 중복 제거
            searchHistory.add(0, query); // 맨 앞에 추가

            // 최대 10개까지만 저장
            if (searchHistory.size() > 10) {
                searchHistory = searchHistory.subList(0, 10);
            }
        }
    }

    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }

    public void clearSearchHistory() {
        searchHistory.clear();
    }
}