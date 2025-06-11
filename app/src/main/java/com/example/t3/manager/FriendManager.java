package com.example.t3.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.t3.BuildConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 친구 초대 코드 및 친구 목록 관리 매니저 (한 명만 연결 가능)
 */
public class FriendManager {
    private static final String TAG = "FriendManager";
    private static FriendManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private static final String PREF_NAME = "friend_manager_prefs";
    private static final String KEY_MY_INVITE_CODE = "my_invite_code";
    private static final String KEY_CONNECTED_FRIEND = "connected_friend";

    // 친구 정보 클래스
    public static class Friend {
        public String inviteCode;
        public String name;
        public String profileImageUrl;
        public String status; // online, offline
        public long connectedAt;

        public Friend(String inviteCode, String name, String profileImageUrl) {
            this.inviteCode = inviteCode;
            this.name = name;
            this.profileImageUrl = profileImageUrl;
            this.status = "offline";
            this.connectedAt = System.currentTimeMillis();
        }

        // 기본 생성자 (Gson용)
        public Friend() {}
    }

    private FriendManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        // 초대 코드가 없으면 생성
        if (getMyInviteCode() == null) {
            generateInviteCode();
        }
    }

    public static synchronized FriendManager getInstance(Context context) {
        if (instance == null) {
            instance = new FriendManager(context);
        }
        return instance;
    }

    /**
     * 나의 초대 코드 가져오기
     */
    public String getMyInviteCode() {
        return prefs.getString(KEY_MY_INVITE_CODE, null);
    }

    /**
     * 초대 코드 생성
     */
    private void generateInviteCode() {
        String code = generateUniqueCode();
        prefs.edit().putString(KEY_MY_INVITE_CODE, code).apply();
        Log.d(TAG, "초대 코드 생성됨: " + code);
    }

    /**
     * 고유한 초대 코드 생성
     */
    private String generateUniqueCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder("SHOP");

        // 4자리 랜덤 숫자 추가
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }

    /**
     * 친구 추가 (한 명만 가능)
     */
    public boolean addFriend(String inviteCode) {
        try {
            // 이미 친구가 있으면 추가 불가
            if (hasConnectedFriend()) {
                Log.d(TAG, "이미 연결된 친구가 있어서 추가할 수 없습니다");
                return false;
            }

            // 가상의 친구 정보 생성 (실제로는 서버에서 가져와야 함)
            Friend newFriend = createMockFriend(inviteCode);

            // 친구 저장
            saveConnectedFriend(newFriend);

            Log.d(TAG, "친구 추가됨: " + newFriend.name);
            return true;

        } catch (Exception e) {
            Log.e(TAG, "친구 추가 실패", e);
            return false;
        }
    }

    /**
     * 테스트용 친구 추가 (개발 환경에서만)
     */
    public boolean addTestFriend(String name, String inviteCode, String status) {
        if (!BuildConfig.DEBUG) {
            return false;
        }

        // 이미 친구가 있으면 추가 불가
        if (hasConnectedFriend()) {
            return false;
        }

        Friend testFriend = new Friend(inviteCode, name, null);
        testFriend.status = status;

        saveConnectedFriend(testFriend);
        Log.d(TAG, "테스트 친구 추가됨: " + name);
        return true;
    }

    /**
     * 가상의 친구 정보 생성 (테스트용)
     */
    private Friend createMockFriend(String inviteCode) {
        // 실제로는 서버 API를 호출하여 초대 코드로 사용자 정보를 가져와야 함
        Map<String, String> mockFriends = new HashMap<>();
        mockFriends.put("SHOP1234", "김민수");
        mockFriends.put("SHOP5678", "이영희");
        mockFriends.put("SHOP9012", "박철수");
        mockFriends.put("SHOP3456", "최지민");
        mockFriends.put("SHOP7890", "정하나");

        String name = mockFriends.get(inviteCode);
        if (name == null) {
            // 맵에 없는 코드면 랜덤 이름 생성
            name = "친구" + inviteCode.substring(4);
        }

        Friend friend = new Friend(inviteCode, name, null);

        // 50% 확률로 온라인 상태 설정
        if (new Random().nextBoolean()) {
            friend.status = "online";
        }

        return friend;
    }

    /**
     * 연결된 친구가 있는지 확인
     */
    public boolean hasConnectedFriend() {
        return getConnectedFriend() != null;
    }

    /**
     * 연결된 친구 가져오기 (한 명만)
     */
    public Friend getConnectedFriend() {
        String json = prefs.getString(KEY_CONNECTED_FRIEND, null);
        if (json == null) {
            return null;
        }

        try {
            return gson.fromJson(json, Friend.class);
        } catch (Exception e) {
            Log.e(TAG, "친구 정보 파싱 실패", e);
            return null;
        }
    }

    /**
     * 연결된 친구 목록 가져오기 (리스트 형태로 반환, 호환성 유지)
     */
    public List<Friend> getConnectedFriends() {
        List<Friend> friends = new ArrayList<>();
        Friend connectedFriend = getConnectedFriend();

        if (connectedFriend != null) {
            friends.add(connectedFriend);
        }

        return friends;
    }

    /**
     * 친구 저장
     */
    private void saveConnectedFriend(Friend friend) {
        String json = gson.toJson(friend);
        prefs.edit().putString(KEY_CONNECTED_FRIEND, json).apply();
    }

    /**
     * 초대 코드로 친구 찾기
     */
    public Friend getFriendByCode(String inviteCode) {
        Friend friend = getConnectedFriend();
        if (friend != null && friend.inviteCode.equals(inviteCode)) {
            return friend;
        }
        return null;
    }

    /**
     * 이미 연결된 친구인지 확인
     */
    public boolean isConnected(String inviteCode) {
        Friend friend = getConnectedFriend();
        return friend != null && friend.inviteCode.equals(inviteCode);
    }

    /**
     * 친구 상태 업데이트
     */
    public void updateFriendStatus(String status) {
        Friend friend = getConnectedFriend();
        if (friend != null) {
            friend.status = status;
            saveConnectedFriend(friend);
            Log.d(TAG, "친구 상태 업데이트: " + status);
        }
    }

    /**
     * 친구 연결 해제 (개발 환경에서만 사용)
     */
    public void disconnectFriend() {
        if (BuildConfig.DEBUG) {
            prefs.edit().remove(KEY_CONNECTED_FRIEND).apply();
            Log.d(TAG, "친구 연결 해제됨");
        }
    }

    /**
     * 모든 친구 데이터 초기화 (개발 환경에서만)
     */
    public void clearAllFriends() {
        if (BuildConfig.DEBUG) {
            prefs.edit().remove(KEY_CONNECTED_FRIEND).apply();
            Log.d(TAG, "모든 친구 데이터 초기화됨");
        }
    }

    // 제거된 메서드들 (더 이상 필요 없음)
    // - removeFriend() : 친구 제거 불가
    // - getFriendCount() : 항상 0 또는 1
    // - getOnlineFriendCount() : 필요 시 getConnectedFriend().status로 확인
}