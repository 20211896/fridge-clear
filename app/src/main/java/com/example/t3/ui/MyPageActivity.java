package com.example.t3.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.t3.BuildConfig;
import com.example.t3.LoginLoadingActivity;
import com.example.t3.R;
import com.example.t3.manager.FriendManager;
import com.example.t3.manager.KakaoUserManager;
import com.example.t3.utils.CustomToast;

import java.util.List;

public class MyPageActivity extends AppCompatActivity {

    private FriendManager friendManager;
    private KakaoUserManager kakaoUserManager;

    // 내 정보 뷰들
    private ImageView imageMyProfile;
    private TextView textMyName, textMyEmail;
    private Button btnEditProfile, btnLogout;

    // 초대코드 관련 뷰들
    private TextView textMyInviteCode;
    private Button btnCopyCode, btnShareCode, btnEnterCode;
    private EditText editInviteCode;

    // 친구 관련 뷰들
    private LinearLayout layoutConnectedFriends, layoutFriendsList;
    private LinearLayout layoutEmptyFriendState, layoutSharedAccount;
    private TextView textSharedBalance, textAccountNumber;
    private Button btnManageSharedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("마이페이지");
        }

        // 매니저 초기화
        friendManager = FriendManager.getInstance(this);
        kakaoUserManager = KakaoUserManager.getInstance(this);

        // 뷰 초기화
        initViews();

        // 리스너 설정
        setupListeners();

        // 개발 환경에서 테스트 친구 자동 추가
        if (BuildConfig.DEBUG) {
            addTestFriendAutomatically();
        }

        // 데이터 로드
        loadUserInfo();
        loadInviteCode();
        updateFriendsList();
    }

    /**
     * 테스트용 친구 자동 추가 (개발 환경에서만)
     */
    private void addTestFriendAutomatically() {
        List<FriendManager.Friend> friends = friendManager.getConnectedFriends();

        // 친구가 없으면 테스트 친구 1명 자동 추가
        if (friends.isEmpty()) {
            String name = "김철수";
            String code = "TEST01";
            String status = "online";

            // FriendManager의 테스트 메서드 사용
            boolean success = friendManager.addTestFriend(name, code, status);

            if (success) {
                android.util.Log.d("MyPageActivity", "테스트 친구 자동 추가: " + name);
            }
        }
    }

    private void initViews() {
        // 내 정보
        imageMyProfile = findViewById(R.id.image_my_profile);
        textMyName = findViewById(R.id.text_my_name);
        textMyEmail = findViewById(R.id.text_my_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);

        // 로그아웃 버튼 추가 (레이아웃에 버튼 추가 필요)
        // btnLogout = findViewById(R.id.btn_logout);

        // 초대코드
        textMyInviteCode = findViewById(R.id.text_my_invite_code);
        btnCopyCode = findViewById(R.id.btn_copy_code);
        btnShareCode = findViewById(R.id.btn_share_code);
        btnEnterCode = findViewById(R.id.btn_enter_code);
        editInviteCode = findViewById(R.id.edit_invite_code);

        // 친구 관련
        layoutConnectedFriends = findViewById(R.id.layout_connected_friends);
        layoutFriendsList = findViewById(R.id.layout_friends_list);
        layoutEmptyFriendState = findViewById(R.id.layout_empty_friend_state);

        // 공유 통장
        layoutSharedAccount = findViewById(R.id.layout_shared_account);
        textSharedBalance = findViewById(R.id.text_shared_balance);
        textAccountNumber = findViewById(R.id.text_account_number);
        btnManageSharedAccount = findViewById(R.id.btn_manage_shared_account);
    }

    private void setupListeners() {
        // 프로필 수정 버튼 (카카오 정보 새로고침)
        btnEditProfile.setOnClickListener(v -> loadKakaoUserInfo());

        // 로그아웃 버튼 (레이아웃에 추가 시)
        // btnLogout.setOnClickListener(v -> logout());

        // 복사 버튼
        btnCopyCode.setOnClickListener(v -> copyInviteCode());

        // 카카오톡 공유 버튼
        btnShareCode.setOnClickListener(v -> shareInviteCode());

        // 초대코드 입력 버튼
        btnEnterCode.setOnClickListener(v -> enterInviteCode());

        // 공유 통장 관리 버튼
        btnManageSharedAccount.setOnClickListener(v -> manageSharedAccount());
    }

    private void loadUserInfo() {
        KakaoUserManager.UserInfo userInfo = kakaoUserManager.getSavedUserInfo();

        if (userInfo.isLoggedIn) {
            // 카카오 정보가 있는 경우
            displayUserInfo(userInfo);
        } else {
            // 카카오 정보가 없는 경우 기본 정보 표시
            displayDefaultUserInfo();
        }
    }

    private void displayUserInfo(KakaoUserManager.UserInfo userInfo) {
        // 닉네임 표시
        textMyName.setText(userInfo.nickname);

        // 계정 정보 표시 (이메일 대신 카카오 계정으로)
        textMyEmail.setText("카카오 계정");

        // 프로필 이미지 로드
        if (userInfo.profileImageUrl != null && !userInfo.profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(userInfo.profileImageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .centerCrop())
                    .into(imageMyProfile);
        } else {
            imageMyProfile.setImageResource(R.drawable.ic_person);
        }

        // 버튼 텍스트 변경
        btnEditProfile.setText("카카오 정보 새로고침");
    }

    private void displayDefaultUserInfo() {
        textMyName.setText("사용자");
        textMyEmail.setText("카카오 로그인을 해주세요");
        imageMyProfile.setImageResource(R.drawable.ic_person);
        btnEditProfile.setText("카카오 로그인");
    }

    private void loadKakaoUserInfo() {
        // 카카오 토큰 유효성 확인 후 정보 로드
        kakaoUserManager.checkTokenValidity(new KakaoUserManager.TokenValidityCallback() {
            @Override
            public void onValid() {
                // 토큰이 유효하면 최신 정보 가져오기
                kakaoUserManager.getCurrentUserInfo(new KakaoUserManager.UserInfoCallback() {
                    @Override
                    public void onSuccess(KakaoUserManager.UserInfo userInfo) {
                        runOnUiThread(() -> {
                            displayUserInfo(userInfo);
                            CustomToast.show(MyPageActivity.this, "카카오 정보를 새로고침했습니다!");
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            CustomToast.show(MyPageActivity.this, "정보 새로고침에 실패했습니다");
                            // 저장된 정보로 대체
                            displayUserInfo(kakaoUserManager.getSavedUserInfo());
                        });
                    }
                });
            }

            @Override
            public void onInvalid() {
                // 토큰이 유효하지 않으면 로그인 화면으로 이동
                runOnUiThread(() -> {
                    CustomToast.show(MyPageActivity.this, "로그인이 만료되었습니다. 다시 로그인해주세요.");
                    moveToLoginActivity();
                });
            }
        });
    }

    /**
     * 로그인 화면으로 이동
     */
    private void moveToLoginActivity() {
        Intent intent = new Intent(this, LoginLoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 로그아웃 처리
     */
    private void logout() {
        kakaoUserManager.logout(new KakaoUserManager.LogoutCallback() {
            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    CustomToast.show(MyPageActivity.this, "로그아웃되었습니다");
                    moveToLoginActivity();
                });
            }
        });
    }

    private void loadInviteCode() {
        String myCode = friendManager.getMyInviteCode();
        textMyInviteCode.setText(myCode);
    }

    private void copyInviteCode() {
        String inviteCode = textMyInviteCode.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("초대코드", inviteCode);
        clipboard.setPrimaryClip(clip);

        CustomToast.show(this, "초대코드가 복사되었습니다! (" + inviteCode + ")");
    }

    private void shareInviteCode() {
        String inviteCode = textMyInviteCode.getText().toString();
        String message = "🛒 함께 장보기 앱 초대!\n\n" +
                "공동 장바구니로 함께 쇼핑하고 비용을 나눠보세요!\n\n" +
                "📋 초대코드: " + inviteCode + "\n\n" +
                "앱에서 '마이페이지' → '친구 초대코드 입력'에서 코드를 입력해주세요!";

        try {
            // 카카오톡으로 직접 전송 시도
            Intent kakaoIntent = new Intent(Intent.ACTION_SEND);
            kakaoIntent.setType("text/plain");
            kakaoIntent.setPackage("com.kakao.talk");
            kakaoIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(kakaoIntent);
            CustomToast.show(this, "카카오톡으로 초대코드를 공유했습니다!");

        } catch (Exception e) {
            // 카카오톡이 없으면 일반 공유
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(shareIntent, "초대코드 공유하기"));
            CustomToast.show(this, "초대코드를 공유했습니다!");
        }
    }

    private void enterInviteCode() {
        String enteredCode = editInviteCode.getText().toString().trim().toUpperCase();

        if (enteredCode.isEmpty()) {
            CustomToast.show(this, "초대코드를 입력해주세요");
            return;
        }

        if (enteredCode.length() < 4) {
            CustomToast.show(this, "올바른 초대코드를 입력해주세요");
            return;
        }

        // 내 코드와 같은지 확인
        String myCode = textMyInviteCode.getText().toString();
        if (enteredCode.equals(myCode)) {
            CustomToast.show(this, "본인의 초대코드는 입력할 수 없습니다");
            return;
        }

        // 이미 친구가 연결되어 있는지 확인 (한 명만 연결 가능)
        if (friendManager.hasConnectedFriend()) {
            CustomToast.show(this, "이미 연결된 친구가 있습니다. 한 명만 연결 가능합니다.");
            return;
        }

        // 초대코드 처리
        boolean success = friendManager.addFriend(enteredCode);

        if (success) {
            // 입력 필드 초기화
            editInviteCode.setText("");

            FriendManager.Friend newFriend = friendManager.getConnectedFriend();
            if (newFriend != null) {
                CustomToast.show(this, newFriend.name + "님과 연결되었습니다! 🎉");
            }

            // 친구 목록 업데이트
            updateFriendsList();
        } else {
            CustomToast.show(this, "연결에 실패했습니다. 올바른 초대코드인지 확인해주세요.");
        }
    }

    private void updateFriendsList() {
        List<FriendManager.Friend> friends = friendManager.getConnectedFriends();

        if (friends.isEmpty()) {
            // 친구가 없는 경우
            layoutConnectedFriends.setVisibility(View.GONE);
            layoutSharedAccount.setVisibility(View.GONE);
            layoutEmptyFriendState.setVisibility(View.VISIBLE);
        } else {
            // 친구가 있는 경우
            layoutEmptyFriendState.setVisibility(View.GONE);
            layoutConnectedFriends.setVisibility(View.VISIBLE);
            layoutSharedAccount.setVisibility(View.VISIBLE);

            // 친구 목록 업데이트
            layoutFriendsList.removeAllViews();
            for (FriendManager.Friend friend : friends) {
                addFriendItemView(friend);
            }

            // 공유 통장 정보 업데이트
            updateSharedAccountInfo();
        }
    }

    private void addFriendItemView(FriendManager.Friend friend) {
        View friendView = LayoutInflater.from(this).inflate(R.layout.friend_item_layout, layoutFriendsList, false);

        TextView textFriendName = friendView.findViewById(R.id.text_friend_name);
        TextView textFriendStatus = friendView.findViewById(R.id.text_friend_status);
        View statusDot = friendView.findViewById(R.id.view_status_dot);

        // 해제 버튼 처리
        Button btnDisconnect = friendView.findViewById(R.id.btn_disconnect);
        if (btnDisconnect != null) {
            // 개발 환경에서만 해제 버튼 보이기
            if (BuildConfig.DEBUG) {
                btnDisconnect.setOnClickListener(v -> disconnectFriend(friend));
            } else {
                btnDisconnect.setVisibility(View.GONE);
            }
        }

        textFriendName.setText(friend.name + " (카카오톡)"); // 카카오톡 닉네임임을 표시

        // 상태에 따라 색상 변경
        if ("online".equals(friend.status)) {
            textFriendStatus.setText("함께 장보는 중");
            textFriendStatus.setTextColor(getResources().getColor(R.color.green, null));
            statusDot.setBackgroundTintList(getResources().getColorStateList(R.color.green, null));
        } else {
            textFriendStatus.setText("오프라인");
            textFriendStatus.setTextColor(getResources().getColor(R.color.gray_666, null));
            statusDot.setBackgroundTintList(getResources().getColorStateList(R.color.gray_666, null));
        }

        layoutFriendsList.addView(friendView);
    }

    private void disconnectFriend(FriendManager.Friend friend) {
        // 개발 환경에서만 친구 해제 가능
        if (BuildConfig.DEBUG) {
            friendManager.disconnectFriend();
            CustomToast.show(this, friend.name + "님과의 연결을 해제했습니다");
            updateFriendsList();
        }
    }

    private void updateSharedAccountInfo() {
        // 친구가 연결되어 있을 때만 공유 통장 정보 표시
        if (friendManager.hasConnectedFriend()) {
            // 친구 한 명과 나 = 2명의 공유 통장
            int baseBalance = 25000;
            int totalBalance = baseBalance * 2; // 나 + 친구 1명

            textSharedBalance.setText(String.format("%,d원", totalBalance));
            textAccountNumber.setText("1234-567-890123");
        }
    }

    private void manageSharedAccount() {
        CustomToast.show(this, "공유 통장 관리 기능은 준비중입니다");
        // 실제로는 공유 통장 관리 화면으로 이동
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면 복귀 시 친구 목록 새로고침
        updateFriendsList();
    }
}