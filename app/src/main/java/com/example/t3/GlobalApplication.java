
package com.example.t3;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 카카오 SDK 초기화
        KakaoSdk.init(this, "c5603a44abd3c2473610b8ec7a36c046");
    }
}