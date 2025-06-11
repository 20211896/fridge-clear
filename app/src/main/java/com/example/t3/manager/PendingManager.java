package com.example.t3.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.t3.model.PendingItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PendingManager {
    private static PendingManager instance;
    private final List<PendingItem> pendingItems;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "pending_prefs";
    private static final String KEY_PENDING = "pending_items";

    private PendingManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pendingItems = loadFromPrefs();
    }

    public static synchronized PendingManager getInstance(Context context) {
        if (instance == null) {
            instance = new PendingManager(context);
        }
        return instance;
    }

    private List<PendingItem> loadFromPrefs() {
        String json = prefs.getString(KEY_PENDING, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<PendingItem>>() {}.getType();
        List<PendingItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    private void saveToPrefs() {
        String json = gson.toJson(pendingItems);
        prefs.edit().putString(KEY_PENDING, json).apply();
    }

    public void addPendingItem(PendingItem item) {
        pendingItems.add(item);
        saveToPrefs();
    }

    public void removePendingItem(PendingItem item) {
        pendingItems.remove(item);
        saveToPrefs();
    }

    public void updatePendingItem(PendingItem item) {
        int idx = -1;
        for (int i = 0; i < pendingItems.size(); i++) {
            if (pendingItems.get(i).getId().equals(item.getId())) {
                idx = i;
                break;
            }
        }
        if (idx >= 0) {
            pendingItems.set(idx, item);
            saveToPrefs();
        }
    }

    public List<PendingItem> getPendingItems() {
        return new ArrayList<>(pendingItems);
    }

    /**
     * 승인대기 상태의 아이템들만 반환
     */
    public List<PendingItem> getPendingOnlyItems() {
        List<PendingItem> result = new ArrayList<>();
        for (PendingItem item : pendingItems) {
            if (item.isPending()) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 승인된(공동장바구니) 상태의 아이템들만 반환
     */
    public List<PendingItem> getApprovedItems() {
        List<PendingItem> result = new ArrayList<>();
        for (PendingItem item : pendingItems) {
            if (item.isApproved()) {
                result.add(item);
            }
        }
        return result;
    }
}