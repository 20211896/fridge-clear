package com.example.t3.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

/**
 * 카카오톡 사용자 정보 관리 매니저
 * MainActivity의 기존 카카오 연동 방식과 호환
 */
public class KakaoUserManager {
    private static final String TAG = "KakaoUserManager";
    private static KakaoUserManager instance;
    private final SharedPreferences prefs;
    private static final String PREF_NAME = "kakao_user_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_PROFILE_IMAGE = "profile_image_url";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public static class UserInfo {
        public String userId;
        public String nickname;
        public String profileImageUrl;
        public boolean isLoggedIn;

        public UserInfo(String userId, String nickname, String profileImageUrl, boolean isLoggedIn) {
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
            this.isLoggedIn = isLoggedIn;
        }
    }

    private KakaoUserManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized KakaoUserManager getInstance(Context context) {
        if (instance == null) {
            instance = new KakaoUserManager(context);
        }
        return instance;
    }

    /**
     * 현재 로그인된 사용자 정보 가져오기 (MainActivity 방식과 동일)
     */
    public void getCurrentUserInfo(UserInfoCallback callback) {
        // MainActivity와 동일한 방식으로 카카오 API 호출
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error);

                // 토큰이 유효하지 않으면 저장된 정보 반환
                UserInfo savedInfo = getSavedUserInfo();
                if (savedInfo.isLoggedIn) {
                    callback.onSuccess(savedInfo);
                } else {
                    callback.onFailure("로그인이 필요합니다: " + error.getMessage());
                }
            } else if (user != null) {
                Log.i(TAG, "사용자 정보 요청 성공");

                // MainActivity와 동일한 방식으로 사용자 정보 추출
                UserInfo userInfo = extractUserInfoFromKakaoUser(user);

                // 정보 저장
                saveUserInfo(userInfo.userId, userInfo.nickname, userInfo.profileImageUrl, true);

                callback.onSuccess(userInfo);
            }
            return null;
        });
    }

    /**
     * 카카오 User 객체에서 사용자 정보 추출 (MainActivity 로직과 동일)
     */
    private UserInfo extractUserInfoFromKakaoUser(User user) {
        String userId = String.valueOf(user.getId());
        String nickname = "카카오 사용자";
        String profileImageUrl = null;

        try {
            // MainActivity와 동일한 로직
            if (user.getKakaoAccount() != null) {
                if (user.getKakaoAccount().getProfile() != null) {
                    String kakaoNickname = user.getKakaoAccount().getProfile().getNickname();
                    if (kakaoNickname != null && !kakaoNickname.isEmpty()) {
                        nickname = kakaoNickname;
                    }

                    profileImageUrl = user.getKakaoAccount().getProfile().getProfileImageUrl();
                }
            }

            Log.d(TAG, "추출된 사용자 정보 - 닉네임: " + nickname + ", 프로필 이미지: " + profileImageUrl);

        } catch (Exception e) {
            Log.e(TAG, "사용자 정보 추출 중 오류", e);
        }

        return new UserInfo(userId, nickname, profileImageUrl, true);
    }

    /**
     * MainActivity에서 호출할 수 있는 사용자 정보 직접 저장 메서드
     */
    public void saveUserInfoFromMainActivity(User user) {
        try {
            UserInfo userInfo = extractUserInfoFromKakaoUser(user);
            saveUserInfo(userInfo.userId, userInfo.nickname, userInfo.profileImageUrl, true);
            Log.d(TAG, "MainActivity에서 사용자 정보 저장 완료");
        } catch (Exception e) {
            Log.e(TAG, "MainActivity 사용자 정보 저장 중 오류", e);
        }
    }

    /**
     * 사용자 정보 저장
     */
    private void saveUserInfo(String userId, String nickname, String profileImageUrl, boolean isLoggedIn) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_NICKNAME, nickname)
                .putString(KEY_PROFILE_IMAGE, profileImageUrl)
                .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
                .apply();

        Log.d(TAG, "사용자 정보 저장 완료: " + nickname);
    }

    /**
     * 저장된 사용자 정보 가져오기
     */
    public UserInfo getSavedUserInfo() {
        String userId = prefs.getString(KEY_USER_ID, "");
        String nickname = prefs.getString(KEY_NICKNAME, "카카오 사용자");
        String profileImageUrl = prefs.getString(KEY_PROFILE_IMAGE, null);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        return new UserInfo(userId, nickname, profileImageUrl, isLoggedIn);
    }

    /**
     * 로그인 상태 확인
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 토큰 유효성 확인 (MainActivity 방식과 동일)
     */
    public void checkTokenValidity(TokenValidityCallback callback) {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Log.e(TAG, "토큰 유효성 확인 실패", error);
                callback.onInvalid();
            } else {
                Log.i(TAG, "토큰 유효함");
                // 유효한 사용자 정보를 저장
                if (user != null) {
                    saveUserInfoFromMainActivity(user);
                }
                callback.onValid();
            }
            return null;
        });
    }

    /**
     * 로그아웃 처리 (MainActivity와 호환)
     */
    public void logout(LogoutCallback callback) {
        UserApiClient.getInstance().logout(error -> {
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error);
            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨");
            }

            // 로컬 정보 삭제
            clearUserInfo();
            callback.onComplete();
            return null;
        });
    }

    /**
     * 사용자 정보 삭제
     */
    public void clearUserInfo() {
        prefs.edit().clear().apply();
        Log.d(TAG, "로컬 사용자 정보 삭제됨");
    }

    /**
     * MainActivity에서 네비게이션 헤더 업데이트용 헬퍼 메서드
     */
    public void updateNavigationHeader(NavigationHeaderCallback callback) {
        getCurrentUserInfo(new UserInfoCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                callback.onUpdateHeader(userInfo.nickname, userInfo.profileImageUrl);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "네비게이션 헤더 업데이트 실패: " + error);
                callback.onUpdateHeader("사용자", null);
            }
        });
    }

    /**
     * 현재 로그인된 사용자의 닉네임 가져오기 (간단한 접근용)
     */
    public String getCurrentUserNickname() {
        UserInfo userInfo = getSavedUserInfo();
        return userInfo.nickname;
    }

    /**
     * 현재 로그인된 사용자의 프로필 이미지 URL 가져오기 (간단한 접근용)
     */
    public String getCurrentUserProfileImageUrl() {
        UserInfo userInfo = getSavedUserInfo();
        return userInfo.profileImageUrl;
    }

    /**
     * 사용자 정보 콜백 인터페이스
     */
    public interface UserInfoCallback {
        void onSuccess(UserInfo userInfo);
        void onFailure(String error);
    }

    /**
     * 토큰 유효성 콜백 인터페이스
     */
    public interface TokenValidityCallback {
        void onValid();
        void onInvalid();
    }

    /**
     * 로그아웃 콜백 인터페이스
     */
    public interface LogoutCallback {
        void onComplete();
    }

    /**
     * 네비게이션 헤더 업데이트 콜백 인터페이스
     */
    public interface NavigationHeaderCallback {
        void onUpdateHeader(String nickname, String profileImageUrl);
    }
}