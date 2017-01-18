package com.feicuiedu.hunttreasure.net;

import com.feicuiedu.hunttreasure.treasure.Area;
import com.feicuiedu.hunttreasure.treasure.Treasure;
import com.feicuiedu.hunttreasure.treasure.detail.TreasureDetail;
import com.feicuiedu.hunttreasure.treasure.detail.TreasureDetailResult;
import com.feicuiedu.hunttreasure.treasure.hide.HideTreasure;
import com.feicuiedu.hunttreasure.treasure.hide.HideTreasureResult;
import com.feicuiedu.hunttreasure.user.User;
import com.feicuiedu.hunttreasure.user.account.Update;
import com.feicuiedu.hunttreasure.user.account.UpdateResult;
import com.feicuiedu.hunttreasure.user.account.UploadResult;
import com.feicuiedu.hunttreasure.user.login.LoginResult;
import com.feicuiedu.hunttreasure.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Administrator on 2017/1/9 0009.
 */

public interface TreasureApi {
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);

    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>>getTreasureInArea(@Body Area area);
    //宝藏详情的请求
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>>getTreasureDetail(@Body TreasureDetail treasureDetail);
    //埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult>hideTreasure(@Body HideTreasure hideTreasure);

    //上传头像
//    @Multipart 第二种构建方式
//    @POST("/Handler/UserLoadPicHandler1.ashx")
//    Call<UploadResult>upload(@Part("file\";filename=\"image,png\"") RequestBody body);
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);


    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult>updata(@Body Update update);
}
