package com.feicuiedu.hunttreasure.user.account;

import android.util.Log;

import com.feicuiedu.hunttreasure.net.NetClient;
import com.feicuiedu.hunttreasure.user.UserPrefs;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/13 0013.
 */

public class AccountPresenter {
    private AccountView accountView;
    public AccountPresenter(AccountView accountView) {
        this.accountView = accountView;
    }
    public void uploadPhoto(File file){
        accountView.showProgress();
        //进度显示
        MultipartBody.Part part=MultipartBody.Part.createFormData("file","photo.png", RequestBody.create(null,file));
        //上传的请求
        Call<UploadResult> uploadResultCall = NetClient.getInstances().getTreasureApi().upload(part);

        uploadResultCall.enqueue(mResultCallback);
    }

    private Callback<UploadResult> mResultCallback=new Callback<UploadResult>() {
        @Override
        public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
            if (response.isSuccessful()) {
                UploadResult body = response.body();
                if (body==null) {
                    //提示
                    return;
                }
                //提示
                accountView.showMessage(body.getMsg());
                if (body.getCount()!=1) {
                    return;
                }
                String photoUrl = body.getUrl();
                UserPrefs.getInstance().setPhoto(NetClient.BASE_URL+photoUrl);
                //重更新信息!!新加载到个人信息上 | 保存到用户信息
                accountView.updataPhoto(NetClient.BASE_URL+photoUrl);
                Log.i("TAG","photo:"+photoUrl);

                String substring = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.length());

                Update update=new Update(UserPrefs.getInstance().getTokenid(),substring);


                Call<UpdateResult> updata = NetClient.getInstances().getTreasureApi().updata(update);
                updata.enqueue(updateResultCallback);
            }
        }
        @Override
        public void onFailure(Call<UploadResult> call, Throwable t) {
                //提示
            accountView.hideProgress();
            accountView.showMessage("请求失败"+t.getMessage());
        }

    };
    private Callback<UpdateResult> updateResultCallback=new Callback<UpdateResult>() {
        @Override
        public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {
            accountView.hideProgress();
            if (response.isSuccessful()) {
                UpdateResult result = response.body();
                if (result==null) {
                    accountView.showMessage("未知的错误");
                    return;
                }
                accountView.showMessage(result.getMsg());
                if (result.getCode()!=1) {
                    return;
                }
            }
        }
        @Override
        public void onFailure(Call<UpdateResult> call, Throwable t) {
            accountView.hideProgress();
            accountView.showMessage("更新失败"+t.getMessage());
        }
    };
}
