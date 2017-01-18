package com.feicuiedu.hunttreasure.treasure.hide;

import com.feicuiedu.hunttreasure.net.NetClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/12 0012.
 */
//埋藏宝藏业务类
public class HideTreasurePresenter {
    private HideTreasureView mHideView;

    public HideTreasurePresenter(HideTreasureView mHideView) {
        this.mHideView = mHideView;
    }

    public void hideTreasure(HideTreasure hideTreasure){
        mHideView.showProgress();
        Call<HideTreasureResult> resultCall = NetClient.getInstances().getTreasureApi().hideTreasure(hideTreasure);
        resultCall.enqueue(mResultCallback);

    }
    private Callback<HideTreasureResult> mResultCallback=new Callback<HideTreasureResult>() {

        @Override
        public void onResponse(Call<HideTreasureResult> call, Response<HideTreasureResult> response) {
            //隐藏进度
            mHideView.hodeProgress();
            if (response.isSuccessful()) {
                HideTreasureResult treasureResult = response.body();
                if (treasureResult==null) {
                    mHideView.showMessage("未知的错误");
                    return;
                }
                //上传成功
                if (treasureResult.getCode()==1) {
                    //跳转回首页
                    mHideView.navigationToHome();
                }
                //提示:
                mHideView.showMessage(treasureResult.getMsg());
            }
        }

        @Override
        public void onFailure(Call<HideTreasureResult> call, Throwable t) {

            mHideView.hodeProgress();
            mHideView.showMessage("请求失败"+t.getMessage());
        }
    };
}
