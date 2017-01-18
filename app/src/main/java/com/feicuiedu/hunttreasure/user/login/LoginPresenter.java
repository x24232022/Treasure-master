package com.feicuiedu.hunttreasure.user.login;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.feicuiedu.hunttreasure.net.NetClient;
import com.feicuiedu.hunttreasure.user.User;
import com.feicuiedu.hunttreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public class LoginPresenter {
    private LoginView mLoginView;

    public LoginPresenter(LoginView mLoginView) {
        this.mLoginView = mLoginView;
    }

    public void login(User user) {
        mLoginView.showProgress();
        Call<LoginResult> loginResultCall = NetClient.getInstances().getTreasureApi().login(user);
        loginResultCall.enqueue(mCallback);
    }

    @NonNull
    private Callback<LoginResult> mCallback = new Callback<LoginResult>() {
        @Override
        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
            mLoginView.hideProgress();
            if(response.isSuccessful()){
                LoginResult loginResult = response.body();
                if (loginResult==null) {
                    mLoginView.showMessage("未知的错误");
                    return;
                }
                if(loginResult.getCode()==1){

                    UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+loginResult.getHeadpic());
                    UserPrefs.getInstance().setTokenid(loginResult.getTokenid());

                    mLoginView.navgationToHome();
                }
                mLoginView.showMessage(loginResult.getMsg());
            }
            
        }

        @Override
        public void onFailure(Call<LoginResult> call, Throwable t) {
            mLoginView.hideProgress();
            mLoginView.showMessage("请求失败"+t.getMessage());
        }
    };

}
