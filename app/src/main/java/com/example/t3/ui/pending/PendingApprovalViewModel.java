package com.example.t3.ui.pending;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.t3.manager.PendingManager;
import com.example.t3.model.PendingItem;

import java.util.List;

public class PendingApprovalViewModel extends AndroidViewModel {
    private final PendingManager manager;
    private final MutableLiveData<List<PendingItem>> pendingItems = new MutableLiveData<>();

    public PendingApprovalViewModel(@NonNull Application application) {
        super(application);
        manager = PendingManager.getInstance(application);
        pendingItems.setValue(manager.getPendingItems());
    }

    public LiveData<List<PendingItem>> getPendingItems() {
        return pendingItems;
    }

    public void addPendingItem(PendingItem item) {
        manager.addPendingItem(item);
        pendingItems.setValue(manager.getPendingItems());
    }

    public void removePendingItem(PendingItem item) {
        manager.removePendingItem(item);
        pendingItems.setValue(manager.getPendingItems());
    }

    /**
     * 기존 PendingItem 업데이트 (상태 변경 등)
     */
    public void updatePendingItem(PendingItem updatedItem) {
        manager.updatePendingItem(updatedItem);
        pendingItems.setValue(manager.getPendingItems());
    }

    public void refreshItems() {
        pendingItems.setValue(manager.getPendingItems());
    }
}