package com.feicuiedu.hunttreasure.treasure.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.custom.TreasureView;
import com.feicuiedu.hunttreasure.treasure.Treasure;
import com.feicuiedu.hunttreasure.treasure.map.MapFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//宝藏的详情页
public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView{

    @BindView(R.id.toolbar)
    Toolbar mToobar;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.detail_treasure)
    TreasureView mTreasureView;
    @BindView(R.id.tv_detail_description)
    TextView mTvDetail;

    private ActivityUtils mActivityUtils;
    private Treasure mTreasure;
    private static final String KEY_TREASURE = "key_treasure";
    private TreasureDetailPresenter mTreasureDetailPresenter;
    private String endArr;
    private LatLng end;
    private String startAddr;
    private LatLng start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
        ButterKnife.bind(this);
    }

    /**
     * 对外提供一个跳转到本页面的方法
     * 1.规范传递的数据,需要的数据必须传入
     * 2.key的用法简单化
     */
    public static void open(Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mActivityUtils=new ActivityUtils(this);
        mTreasureDetailPresenter = new TreasureDetailPresenter(this);
        ButterKnife.bind(this);
        //拿到跳转界面的数据,传递实体类需要调用getSerializableExtra
        mTreasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);
        setSupportActionBar(mToobar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(mTreasure.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        initMapView();
        mTreasureView.bindTreasure(mTreasure);

        //进行网络获取得到宝藏的详情
        TreasureDetail treasureDetail = new TreasureDetail(mTreasure.getId());
        mTreasureDetailPresenter.getTreasureDetail(treasureDetail);

    }

    private void initMapView() {
        LatLng latLng=new LatLng(mTreasure.getLatitude(),mTreasure.getLongitude());
        //地图的状态
        MapStatus mapStatus=new MapStatus.Builder()
                .target(latLng)
                .overlook(-20)
                .zoom(18)
                .rotate(0)
                .build();
        BaiduMapOptions options=new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .scrollGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .scrollGesturesEnabled(false)
                .rotateGesturesEnabled(false);

        MapView mapView=new MapView(this,options);
        //填充到布局中
        mFrameLayout.addView(mapView);
        BaiduMap map = mapView.getMap();
        //添加覆盖物
        BitmapDescriptor dot_expand= BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
        MarkerOptions marker=new MarkerOptions()
                .position(latLng)
                .icon(dot_expand)
                .anchor(0.5f,0.5f);
        map.addOverlay(marker);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    //导航图标点击
    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view){
        //创建
        PopupMenu popupMenu=new PopupMenu(this,view);
        //菜单填充
        popupMenu.inflate(R.menu.menu_navigation);
        //监听
        popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);
        //显示
        popupMenu.show();
    }
    private PopupMenu.OnMenuItemClickListener mMenuItemClickListener=new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            start = MapFragment.getMyLocation();
            startAddr = MapFragment.getLocationAddr();
            end = new LatLng(mTreasure.getLatitude(),mTreasure.getLongitude());
            endArr = mTreasure.getLocation();

            switch (item.getItemId()) {
                case R.id.walking_navi:

                    startWalkingNavi(start, startAddr, end, endArr);
                    break;
                case R.id.biking_navi:
                    startBikingNavi(start, startAddr, end, endArr);
                    break;
            }

            return false;
        }
    };
    //步行导航
    public void startWalkingNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr){
        //导航起点终点的设置
        NaviParaOption option=new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(option, this);
        if (!walkNavi) {
            showDialog();
        }
    }
    //开启网页导航
    private void startWebNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr) {
        NaviParaOption option=new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        BaiduMapNavigation.openWebBaiduMapNavi(option, this);
    }

    //骑行导航
    public void startBikingNavi(LatLng startPoint,String startAddr,LatLng endPoint,String endAddr){
        //导航起点终点的设置
        NaviParaOption option=new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        boolean bikeNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);
        if (!bikeNavi) {
            showDialog();
        }
    }
    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您未安装百度地图App或者版本太低,是否安装")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        OpenClientUtil.getLatestBaiduMapApp(TreasureDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        startWebNavi(start,startAddr,end,endArr);
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
//==================================视图接口实现方法===============================
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setData(List<TreasureDetailResult> resultList) {
        if (resultList.size()>=1) {
            TreasureDetailResult result = resultList.get(0);
            mTvDetail.setText(result.description);
            return;
        }
        mTvDetail.setText("当前的宝藏没有详情信息");
    }
}
