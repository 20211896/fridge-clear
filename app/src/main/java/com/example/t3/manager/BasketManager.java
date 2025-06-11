package com.example.t3.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.t3.model.BasketItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BasketManager {
    private static BasketManager instance;
    private final List<BasketItem> myBasketItems;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "basket_prefs";
    private static final String KEY_BASKET = "basket_items";

    private BasketManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        myBasketItems = loadFromPrefs();
    }

    public static synchronized BasketManager getInstance(Context context) {
        if (instance == null) {
            instance = new BasketManager(context);
        }
        return instance;
    }

    private List<BasketItem> loadFromPrefs() {
        String json = prefs.getString(KEY_BASKET, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<BasketItem>>() {}.getType();
        List<BasketItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    private void saveToPrefs() {
        String json = gson.toJson(myBasketItems);
        prefs.edit().putString(KEY_BASKET, json).apply();
    }

    public void addMyBasketItem(BasketItem item) {
        myBasketItems.add(item);
        saveToPrefs();
    }

    public void removeMyBasketItem(BasketItem item) {
        myBasketItems.remove(item);
        saveToPrefs();
    }

    public void updateMyBasketItem(BasketItem item, int newQty) {
        int idx = myBasketItems.indexOf(item);
        if (idx >= 0) {
            myBasketItems.get(idx).setQuantity(newQty);
            saveToPrefs();
        }
    }

    public List<BasketItem> getMyBasketItems() {
        return new ArrayList<>(myBasketItems);
    }
}
