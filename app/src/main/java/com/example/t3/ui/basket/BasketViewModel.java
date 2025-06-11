package com.example.t3.ui.basket;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.t3.manager.BasketManager;
import com.example.t3.model.BasketItem;

import java.util.List;

public class BasketViewModel extends AndroidViewModel {
    private final BasketManager manager;
    private final MutableLiveData<List<BasketItem>> myItems = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);
        manager = BasketManager.getInstance(application);
        myItems.setValue(manager.getMyBasketItems());
    }

    public LiveData<List<BasketItem>> getMyBasketItems() {
        return myItems;
    }

    public void addItem(BasketItem item) {
        manager.addMyBasketItem(item);
        myItems.setValue(manager.getMyBasketItems());
    }

    public void removeItem(BasketItem item) {
        manager.removeMyBasketItem(item);
        myItems.setValue(manager.getMyBasketItems());
    }

    public void updateItemQuantity(BasketItem item, int qty) {
        manager.updateMyBasketItem(item, qty);
        myItems.setValue(manager.getMyBasketItems());
    }

    // 새로고침 메서드 추가 ⭐
    public void refreshItems() {
        myItems.setValue(manager.getMyBasketItems());
    }
}