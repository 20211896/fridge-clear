package com.example.t3.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.t3.model.PendingItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PendingApprovalManager {
    private static PendingApprovalManager instance;
    private final List<PendingItem> pendingItems;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();
    private static final String PREF_NAME = "pending_approval_prefs";
    private static final String KEY_PENDING = "pending_items";

    private PendingApprovalManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pendingItems = loadFromPrefs();
    }

    public static synchronized PendingApprovalManager getInstance(Context context) {
        if (instance == null) {
            instance = new PendingApprovalManager(context);
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
        // 중복 확인 (같은 ID면 수량 증가)
        for (PendingItem existing : pendingItems) {
            if (existing.getId().equals(item.getId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                saveToPrefs();
                return;
            }
        }
        pendingItems.add(item);
        saveToPrefs();
    }

    public void removePendingItem(PendingItem item) {
        pendingItems.remove(item);
        saveToPrefs();
    }

    public void updatePendingItem(PendingItem item) {
        int idx = pendingItems.indexOf(item);
        if (idx >= 0) {
            pendingItems.set(idx, item);
            saveToPrefs();
        }
    }

    public List<PendingItem> getPendingItems() {
        return new ArrayList<>(pendingItems);
    }
}