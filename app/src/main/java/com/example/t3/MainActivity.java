package com.example.t3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t3.databinding.ActivityMainBinding;
import com.example.t3.manager.KakaoUserManager;
import com.example.t3.ui.MyPageActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // 네비게이션 헤더의 사용자 정보 뷰들
    private CircleImageView profileImage;
    private TextView userName;
    private TextView userEmail;

    // 카카오 사용자 매니저
    private KakaoUserManager kakaoUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // 카카오 사용자 매니저 초기화
        kakaoUserManager = KakaoUserManager.getInstance(this);

        // FAB 클릭 리스너
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = binding.appBarMain.bottomNavView;

        // 네비게이션 헤더 뷰 설정 및 카카오 사용자 정보 초기화
        setupNavigationHeader(navigationView);

        // 드로어와 바텀 네비게이션의 모든 메뉴 ID를 top level destinations로 설정
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    /**
     * 네비게이션 헤더 설정 및 카카오 사용자 정보 로드
     */
    private void setupNavigationHeader(NavigationView navigationView) {
        // 네비게이션 헤더 뷰 가져오기
        View headerView = navigationView.getHeaderView(0);

        // 헤더의 뷰들 초기화
        profileImage = headerView.findViewById(R.id.profile_image);
        userName = headerView.findViewById(R.id.user_name);
        // userEmail = headerView.findViewById(R.id.user_email); // 필요시 추가

        // KakaoUserManager를 통한 사용자 정보 로드
        loadKakaoUserInfoWithManager();
    }

    /**
     * KakaoUserManager를 통해 카카오 사용자 정보 로드
     */
    private void loadKakaoUserInfoWithManager() {
        kakaoUserManager.getCurrentUserInfo(new KakaoUserManager.UserInfoCallback() {
            @Override
            public void onSuccess(KakaoUserManager.UserInfo userInfo) {
                runOnUiThread(() -> updateUserInfoFromManager(userInfo));
            }

            @Override
            public void onFailure(String error) {
                Log.e("MainActivity", "KakaoUserManager 정보 로드 실패: " + error);
                // 실패 시 기존 방식으로 재시도
                loadKakaoUserInfoDirect();
            }
        });
    }

    /**
     * KakaoUserManager의 정보로 UI 업데이트
     */
    private void updateUserInfoFromManager(KakaoUserManager.UserInfo userInfo) {
        try {
            // 닉네임 설정
            userName.setText(userInfo.nickname);

            // 프로필 이미지 설정
            if (userInfo.profileImageUrl != null && !userInfo.profileImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(userInfo.profileImageUrl)
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.default_profile)
                                .error(R.drawable.default_profile)
                                .centerCrop())
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.default_profile);
            }

            Log.d("MainActivity", "KakaoUserManager를 통한 사용자 정보 업데이트 완료");

        } catch (Exception e) {
            Log.e("MainActivity", "사용자 정보 업데이트 중 오류 발생", e);
            userName.setText("사용자");
        }
    }

    /**
     * 기존 방식으로 카카오 사용자 정보를 가져오기 (백업용)
     */
    private void loadKakaoUserInfoDirect() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e("MainActivity", "사용자 정보 요청 실패", error);
                // 오류 발생 시 기본값 설정
                runOnUiThread(() -> {
                    userName.setText("사용자");
                    if (userEmail != null) {
                        userEmail.setText("로그인 정보를 가져올 수 없습니다");
                    }
                });
                return null;
            }

            if (user != null) {
                // UI 업데이트는 메인 스레드에서 실행
                runOnUiThread(() -> {
                    updateUserInfoDirect(user);
                    // 성공한 정보를 KakaoUserManager에도 저장
                    saveUserInfoToManager(user);
                });
            }
            return null;
        });
    }

    /**
     * 직접 가져온 User 정보로 UI 업데이트 (기존 방식)
     */
    private void updateUserInfoDirect(User user) {
        try {
            // 카카오 계정 정보가 있는지 확인
            if (user.getKakaoAccount() != null) {

                // 프로필 정보 설정
                if (user.getKakaoAccount().getProfile() != null) {
                    String nickname = user.getKakaoAccount().getProfile().getNickname();
                    if (nickname != null && !nickname.isEmpty()) {
                        userName.setText(nickname);
                    } else {
                        userName.setText("카카오 사용자");
                    }

                    // 프로필 이미지 설정
                    String profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(this)
                                .load(profileImageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.default_profile)
                                        .error(R.drawable.default_profile)
                                        .centerCrop())
                                .into(profileImage);
                    }
                }

            } else {
                // 카카오 계정 정보가 없는 경우
                userName.setText("카카오 사용자");
                if (userEmail != null) {
                    userEmail.setText("계정 정보 없음");
                }
            }

            Log.d("MainActivity", "직접 사용자 정보 업데이트 완료");

        } catch (Exception e) {
            Log.e("MainActivity", "사용자 정보 업데이트 중 오류 발생", e);
            userName.setText("사용자");
            if (userEmail != null) {
                userEmail.setText("정보 로드 실패");
            }
        }
    }

    /**
     * 직접 가져온 사용자 정보를 KakaoUserManager에 저장
     */
    private void saveUserInfoToManager(User user) {
        try {
            String userId = String.valueOf(user.getId());
            String nickname = "카카오 사용자";
            String profileImageUrl = null;

            if (user.getKakaoAccount() != null && user.getKakaoAccount().getProfile() != null) {
                nickname = user.getKakaoAccount().getProfile().getNickname() != null
                        ? user.getKakaoAccount().getProfile().getNickname() : "카카오 사용자";
                profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
            }

            // SharedPreferences에 직접 저장
            getSharedPreferences("kakao_user_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("user_id", userId)
                    .putString("nickname", nickname)
                    .putString("profile_image_url", profileImageUrl)
                    .putBoolean("is_logged_in", true)
                    .apply();

            Log.d("MainActivity", "KakaoUserManager에 사용자 정보 저장 완료");

        } catch (Exception e) {
            Log.e("MainActivity", "KakaoUserManager 저장 중 오류", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 메뉴 인플레이트 (마이페이지 메뉴 포함)
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_mypage) {
            // 마이페이지로 이동
            Intent intent = new Intent(this, MyPageActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면이 다시 보여질 때 사용자 정보 새로고침
        if (profileImage != null && userName != null) {
            loadKakaoUserInfoWithManager();
        }
    }
}