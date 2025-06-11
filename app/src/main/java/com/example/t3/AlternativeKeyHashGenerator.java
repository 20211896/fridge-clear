package com.example.t3;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;

public class AlternativeKeyHashGenerator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView titleView = new TextView(this);
        titleView.setText("모든 가능한 Key Hash");
        titleView.setTextSize(20);
        titleView.setPadding(0, 0, 0, 30);
        layout.addView(titleView);

        TextView infoView = new TextView(this);
        infoView.setTextSize(12);
        layout.addView(infoView);

        setContentView(layout);

        generateAllPossibleKeyHashes(infoView);
    }

    private void generateAllPossibleKeyHashes(TextView infoView) {
        StringBuilder info = new StringBuilder();
        StringBuilder allHashes = new StringBuilder();

        info.append("패키지명: ").append(getPackageName()).append("\n\n");

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);

            info.append("서명 개수: ").append(packageInfo.signatures.length).append("\n\n");

            for (int i = 0; i < packageInfo.signatures.length; i++) {
                Signature signature = packageInfo.signatures[i];

                // SHA1 방식 (기본)
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA1");
                    md.update(signature.toByteArray());
                    String keyHashSHA1 = Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();

                    info.append("SHA1 Key Hash ").append(i + 1).append(":\n");
                    info.append(keyHashSHA1).append("\n\n");
                    allHashes.append(keyHashSHA1).append("\n");

                    Log.d("KeyHash", "SHA1 Key Hash " + (i + 1) + ": " + keyHashSHA1);
                } catch (Exception e) {
                    Log.e("KeyHash", "SHA1 생성 실패", e);
                }

                // SHA 방식 (현재 사용 중)
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String keyHashSHA = Base64.encodeToString(md.digest(), Base64.DEFAULT).trim();

                    info.append("SHA Key Hash ").append(i + 1).append(":\n");
                    info.append(keyHashSHA).append("\n\n");
                    allHashes.append(keyHashSHA).append("\n");

                    Log.d("KeyHash", "SHA Key Hash " + (i + 1) + ": " + keyHashSHA);
                } catch (Exception e) {
                    Log.e("KeyHash", "SHA 생성 실패", e);
                }

                // NO_WRAP 옵션으로도 시도
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String keyHashNoWrap = Base64.encodeToString(md.digest(), Base64.NO_WRAP);

                    info.append("SHA Key Hash (NO_WRAP) ").append(i + 1).append(":\n");
                    info.append(keyHashNoWrap).append("\n\n");
                    allHashes.append(keyHashNoWrap).append("\n");

                    Log.d("KeyHash", "SHA Key Hash (NO_WRAP) " + (i + 1) + ": " + keyHashNoWrap);
                } catch (Exception e) {
                    Log.e("KeyHash", "SHA NO_WRAP 생성 실패", e);
                }
            }

            // 모든 Key Hash를 클립보드에 복사
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("All Key Hashes", allHashes.toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "모든 Key Hash가 클립보드에 복사되었습니다", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            info.append("오류 발생: ").append(e.getMessage());
            Log.e("KeyHash", "Key Hash 생성 오류", e);
        }

        infoView.setText(info.toString());
    }
}