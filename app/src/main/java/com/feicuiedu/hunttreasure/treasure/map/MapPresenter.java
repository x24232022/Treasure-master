package com.feicuiedu.hunttreasure.treasure.map;

import com.feicuiedu.hunttreasure.net.NetClient;
import com.feicuiedu.hunttreasure.treasure.Area;
import com.feicuiedu.hunttreasure.treasure.Treasure;
import com.feicuiedu.hunttreasure.treasure.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/1/10 0010.
 */
//获取宝藏数据的业务类

public class MapPresenter {
    private MapMvpView mapMvpView;
    private Area mArea;

    public MapPresenter(MapMvpView mapMvpView) {
        this.mapMvpView = mapMvpView;
    }

    public void getTreasure(Area area){
        if (TreasureRepo.getInstance().isCached(area)) {
            return;
        }
        this.mArea=area;
        Call<List<Treasure>> listCall = NetClient.getInstances().getTreasureApi().getTreasureInArea(area);
        listCall.enqueue(mListCallback);


    }
    private Callback<List<Treasure>> mListCallback=new Callback<List<Treasure>>() {
        @Override
        public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {
            if (response.isSuccessful()) {
                List<Treasure> treasureList = response.body();
                if (treasureList==null) {
                    mapMvpView.showMessage("未知的错误");
                    return;
                }
                //做宝藏数据的缓存处理
                TreasureRepo.getInstance().addTreasure(treasureList);
                TreasureRepo.getInstance().cache(mArea);
                mapMvpView.setData(treasureList);
            }
        }

        @Override
        public void onFailure(Call<List<Treasure>> call, Throwable t) {
            mapMvpView.showMessage("请求失败"+t.getMessage());
        }
    };
}
