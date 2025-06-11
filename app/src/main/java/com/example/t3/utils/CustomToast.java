package com.example.t3.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t3.R;

public class CustomToast {

    /**
     * 안드로이드 아이콘이 없는 깔끔한 커스텀 토스트 표시
     */
    public static void show(Context context, String message) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.custom_toast_layout, null);

            TextView text = layout.findViewById(R.id.toast_text);
            text.setText(message);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 120); // 하단에서 120dp 위에 표시 (낮춤)
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            // 커스텀 토스트 실패 시 기본 토스트로 fallback
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 긴 시간 표시용 커스텀 토스트
     */
    public static void showLong(Context context, String message) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.custom_toast_layout, null);

            TextView text = layout.findViewById(R.id.toast_text);
            text.setText(message);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 120);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}