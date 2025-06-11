package com.example.t3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // 네비게이션 헤더의 사용자 정보 뷰들
    private CircleImageView profileImage;
    private TextView userName;
    private TextView userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

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

        // 카카오 사용자 정보 로드
        loadKakaoUserInfo();
    }

    /**
     * 카카오 사용자 정보를 가져와서 UI에 반영
     */
    private void loadKakaoUserInfo() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error);
                // 오류 발생 시 기본값 설정
                runOnUiThread(() -> {
                    userName.setText("사용자");
                    userEmail.setText("로그인 정보를 가져올 수 없습니다");
                });
                return null;
            }

            if (user != null) {
                // UI 업데이트는 메인 스레드에서 실행
                runOnUiThread(() -> {
                    updateUserInfo(user);
                });
            }
            return null;
        });
    }

    /**
     * 사용자 정보를 UI에 업데이트
     */
    private void updateUserInfo(User user) {
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
                userEmail.setText("계정 정보 없음");
            }

            Log.d("KakaoLogin", "사용자 정보 업데이트 완료");

        } catch (Exception e) {
            Log.e("KakaoLogin", "사용자 정보 업데이트 중 오류 발생", e);
            userName.setText("사용자");
            userEmail.setText("정보 로드 실패");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        // 화면이 다시 보여질 때 사용자 정보 새로고침 (필요한 경우)
        if (profileImage != null && userName != null && userEmail != null) {
            loadKakaoUserInfo();
        }
    }
}