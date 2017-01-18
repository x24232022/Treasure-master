package com.feicuiedu.hunttreasure;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.feicuiedu.hunttreasure.user.UserPrefs;

/**
 * Created by Administrator on 2017/1/4 0004.
 */

public class TreasureApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UserPrefs.init(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
    }
}
