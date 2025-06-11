package com.example.t3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t3.utils.CustomToast;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.model.ClientError;
import com.kakao.sdk.common.model.ClientErrorCause;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.sdk.v2.auth.BuildConfig;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginLoadingActivity extends AppCompatActivity {

    // private TextView keyHashTextView; // 필요시 주석 해제

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_loading);

        // Key Hash 표시용 TextView 찾기 (없으면 무시)
        // keyHashTextView = findViewById(R.id.keyHashTextView);

        // Key Hash 생성 및 출력
        String keyHash = generateKeyHash();

        Button loginButton = findViewById(R.id.button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startKakaoLogin();
            }
        });

        // 개발 모드: 버튼 길게 누르면 Key Hash 복사
        loginButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Key Hash를 클립보드에 복사
                android.content.ClipboardManager clipboard =
                        (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip =
                        android.content.ClipData.newPlainText("Key Hash", keyHash);
                clipboard.setPrimaryClip(clip);

                CustomToast.show(LoginLoadingActivity.this,
                        "Key Hash가 클립보드에 복사되었습니다:\n" + keyHash);
                return true;
            }
        });
    }

    private String generateKeyHash() {
        String keyHash = "";
        try {
            android.content.pm.PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    android.content.pm.PackageManager.GET_SIGNATURES);

            for (android.content.pm.Signature signature : info.signatures) {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT).trim();

                Log.d("KEYHASH", "=========================");
                Log.d("KEYHASH", "Key Hash: " + keyHash);
                Log.d("KEYHASH", "=========================");

                // TextView가 있으면 화면에도 표시
                /*
                if (keyHashTextView != null) {
                    keyHashTextView.setText("Key Hash: " + keyHash);
                }
                */

                // 개발 중에만 Toast 표시
                if (BuildConfig.DEBUG) {
                    CustomToast.show(this, "Key Hash: " + keyHash);
                }
            }
        } catch (Exception e) {
            Log.e("KEYHASH", "Key Hash 생성 실패: " + e.getMessage(), e);
        }
        return keyHash;
    }

    private void startKakaoLogin() {
        Log.d("LoginLoading", "카카오 로그인 시작");

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(this)) {
            loginWithKakaoTalk();
        } else {
            loginWithKakaoAccount();
        }
    }

    private void loginWithKakaoTalk() {
        UserApiClient.getInstance().loginWithKakaoTalk(this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) {
                if (error != null) {
                    Log.e("LoginLoading", "카카오톡 로그인 실패", error);

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                        Log.d("LoginLoading", "사용자가 카카오톡 로그인을 취소했습니다.");
                        return null;
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    loginWithKakaoAccount();
                } else if (token != null) {
                    Log.d("LoginLoading", "카카오톡 로그인 성공: " + token.getAccessToken());
                    getUserInfo();
                }
                return null;
            }
        });
    }

    private void loginWithKakaoAccount() {
        UserApiClient.getInstance().loginWithKakaoAccount(this, new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken token, Throwable error) {
                if (error != null) {
                    Log.e("LoginLoading", "카카오계정 로그인 실패", error);

                    // 사용자 취소 처리
                    if (error instanceof ClientError && ((ClientError) error).getReason() == ClientErrorCause.Cancelled) {
                        Log.d("LoginLoading", "사용자가 카카오계정 로그인을 취소했습니다.");
                        CustomToast.show(LoginLoadingActivity.this, "로그인이 취소되었습니다.");
                        return null;
                    }

                    // Key Hash 불일치 오류 처리
                    if (error.getMessage() != null && error.getMessage().contains("misconfigured")) {
                        String keyHash = generateKeyHash();
                        CustomToast.show(LoginLoadingActivity.this,
                                "카카오 개발자 콘솔에 다음 Key Hash를 등록하세요:\n" + keyHash);

                        // 클립보드에 자동 복사
                        android.content.ClipboardManager clipboard =
                                (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        android.content.ClipData clip =
                                android.content.ClipData.newPlainText("Key Hash", keyHash);
                        clipboard.setPrimaryClip(clip);

                        Log.e("LoginLoading", "Key Hash 등록 필요: " + keyHash);
                    } else {
                        CustomToast.show(LoginLoadingActivity.this,
                                "로그인 실패: " + (error.getMessage() != null ? error.getMessage() : "알 수 없는 오류"));
                    }

                } else if (token != null) {
                    Log.d("LoginLoading", "카카오계정 로그인 성공: " + token.getAccessToken());
                    getUserInfo();
                }
                return null;
            }
        });
    }

    private void getUserInfo() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable error) {
                if (error != null) {
                    Log.e("LoginLoading", "사용자 정보 요청 실패", error);
                    CustomToast.show(LoginLoadingActivity.this, "사용자 정보 요청 실패");
                } else if (user != null) {
                    Log.d("LoginLoading", "사용자 정보 요청 성공");

                    // 사용자 정보 로그
                    Log.d("LoginLoading", "ID: " + user.getId());

                    if (user.getKakaoAccount() != null) {
                        Log.d("LoginLoading", "이메일: " + user.getKakaoAccount().getEmail());

                        if (user.getKakaoAccount().getProfile() != null) {
                            Log.d("LoginLoading", "닉네임: " + user.getKakaoAccount().getProfile().getNickname());
                            Log.d("LoginLoading", "프로필 이미지: " + user.getKakaoAccount().getProfile().getProfileImageUrl());
                        }
                    }

                    // 로그인 성공 후 메인 액티비티로 이동
                    moveToMainActivity();
                }
                return null;
            }
        });
    }

    private void moveToMainActivity() {
        try {
            CustomToast.show(LoginLoadingActivity.this, "로그인 성공!");
            Intent intent = new Intent(LoginLoadingActivity.this, MainActivity.class);

            // 필요한 경우 사용자 정보를 Intent로 전달
            // intent.putExtra("user_id", userId);
            // intent.putExtra("user_nickname", nickname);

            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e("LoginLoading", "메인 액티비티 이동 오류: " + e.getMessage());
            CustomToast.show(LoginLoadingActivity.this, "화면 이동 오류: " + e.getMessage());
        }
    }
}