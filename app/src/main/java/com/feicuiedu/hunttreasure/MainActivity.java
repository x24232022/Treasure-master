package com.feicuiedu.hunttreasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.treasure.HomeActivity;
import com.feicuiedu.hunttreasure.user.UserPrefs;
import com.feicuiedu.hunttreasure.user.login.LoginActivity;
import com.feicuiedu.hunttreasure.user.register.RegisterActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    public static final  String MAIN_ACTION = "navgateion_to_home";
    private ActivityUtils activityUtils;
    private Unbinder mUbinder;

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUbinder = ButterKnife.bind(this);
        activityUtils=new ActivityUtils(this);
        //判断用户是否登录
        SharedPreferences preferences = getSharedPreferences("user_info",MODE_PRIVATE);
        if (preferences!=null) {
            if(preferences.getInt("key_tokenid",0)== UserPrefs.getInstance().getTokenid()){
                activityUtils.startActivity(HomeActivity.class);
                finish();
            }
        }

        //设置广播过滤器
        IntentFilter intentFilter=new IntentFilter(MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);

    }



    @OnClick({R.id.btn_Register, R.id.btn_Login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_Register:
                activityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.btn_Login:
                activityUtils.startActivity(LoginActivity.class);
                break;
        }
    }
}
