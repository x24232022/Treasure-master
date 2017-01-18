package com.feicuiedu.hunttreasure.treasure.detail;

import com.feicuiedu.hunttreasure.net.NetClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/11 0011.
 */

public class TreasureDetailPresenter {
    private TreasureDetailView mDetaulView;

    public TreasureDetailPresenter(TreasureDetailView mDetaulView) {
        this.mDetaulView = mDetaulView;
    }

    public void getTreasureDetail(TreasureDetail treasureDetail){
        Call<List<TreasureDetailResult>> detailCall = NetClient.getInstances().getTreasureApi().getTreasureDetail(treasureDetail);
        detailCall.enqueue(mListCallback);


    }
    private Callback<List<TreasureDetailResult>> mListCallback=new Callback<List<TreasureDetailResult>>() {
        //请求成功
        @Override
        public void onResponse(Call<List<TreasureDetailResult>> call, Response<List<TreasureDetailResult>> response) {
            if (response.isSuccessful()) {
                List<TreasureDetailResult> resultList = response.body();
                if (resultList==null) {
                    mDetaulView.showMessage("未知的错误");
                    return;
                }
                mDetaulView.setData(resultList);
            }
        }
        //请求失败
        @Override
        public void onFailure(Call<List<TreasureDetailResult>> call, Throwable t) {
            //提示失败
            mDetaulView.showMessage("请求失败"+t.getMessage());
        }
    };
}
