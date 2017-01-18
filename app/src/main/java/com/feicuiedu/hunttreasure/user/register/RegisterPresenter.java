package com.feicuiedu.hunttreasure.user.register;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.feicuiedu.hunttreasure.net.NetClient;
import com.feicuiedu.hunttreasure.user.User;
import com.feicuiedu.hunttreasure.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public class RegisterPresenter {

    private RegisterView mRegisterView;

    public RegisterPresenter(RegisterView mRegisterView) {
        this.mRegisterView = mRegisterView;
    }

    public void register(User user){
       mRegisterView.showProgress();
        Call<RegisterResult> resultCall= NetClient.getInstances().getTreasureApi().register(user);
        resultCall.enqueue(mResultCallback);
    }

    @NonNull
    private Callback<RegisterResult> mResultCallback=new Callback<RegisterResult>() {
        @Override
        public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {

            mRegisterView.hideProgress();
            if(response.isSuccessful()){
                RegisterResult result=response.body();
                if(result==null){
                    mRegisterView.showMessage("发生了未知的错误");
                    return;
                }
                if(result.getCode()==1){

                    UserPrefs.getInstance().setTokenid(result.getTokenId());
                    mRegisterView.navigationToHome();

                }
                mRegisterView.showMessage(result.getMsg());
            }
        }

        @Override
        public void onFailure(Call<RegisterResult> call, Throwable t) {
            mRegisterView.hideProgress();
            mRegisterView.showMessage("请求失败"+t.getMessage());
        }
    };


}
