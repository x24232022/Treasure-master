package com.feicuiedu.hunttreasure.treasure.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.custom.TreasureView;
import com.feicuiedu.hunttreasure.treasure.Area;
import com.feicuiedu.hunttreasure.treasure.Treasure;
import com.feicuiedu.hunttreasure.treasure.TreasureRepo;
import com.feicuiedu.hunttreasure.treasure.detail.TreasureDetailActivity;
import com.feicuiedu.hunttreasure.treasure.hide.HideTreasureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/4 0004.
 */

public class MapFragment extends Fragment implements MapMvpView{
    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    @BindView(R.id.iv_located)
    ImageView mIvLocated;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView ivScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView ivScaleDown;
    @BindView(R.id.tv_located)
    TextView tvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView tvCompass;
    @BindView(R.id.tv_currentLocation)
    TextView mTvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView ivToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText etTreasureTitle;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.treasureView)
    TreasureView mTreasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout mHideTreasure;
    private BaiduMap   mBaiduMap;
    private LocationClient mLocationClient;
    private static LatLng mCurrentLocation;
    private LatLng mCurrentStatua;
    private Marker mCurrentMarker;
    private MapView mapView;
    private ActivityUtils mActivityUtils;
    private boolean mIsFirst=true;
    private MapPresenter mapPresenter;
    private static final int UI_MODE_NORMAL=0;//普通的视图
    private static final int UI_MODE_SECLECT=1;//宝藏选中的视图
    private static final int UI_MODE_HIDE=2;//填埋宝藏视图
    private static int mUIMode=UI_MODE_NORMAL;
    private GeoCoder mGeoCoder;
    private String mCurrentAddr;
    private static String mLocationAddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mActivityUtils=new ActivityUtils(this);
        mapPresenter = new MapPresenter(this);
        initMapView();
        initLocation();
        //地理编码的初始化
        initGeoCoder();
    }

    private void initGeoCoder() {
        //初始化创建出地理编码查询的对象
        mGeoCoder = GeoCoder.newInstance();
        //查询的对象设置结果的监听:地理编码的监听
        mGeoCoder.setOnGetGeoCodeResultListener(mGetGeoCoderResultListener);
    }
    private OnGetGeoCoderResultListener mGetGeoCoderResultListener=new OnGetGeoCoderResultListener() {
        //得到地理编码的结果:地址-->经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }
        //得到反向地理编码的结果:经纬度-->地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            //拿到结果后给卡片文本设置上
            if (reverseGeoCodeResult==null) {
                mCurrentAddr = "未知的位置";
                return;
            }
            //拿到反地理编码的得到的位置信息
            mCurrentAddr=reverseGeoCodeResult.getAddress();
            //将地址信息给宝藏卡片标题TextView设置上
            mTvCurrentLocation.setText(mCurrentAddr);
        }
    };

    //初始化定位
    private void initLocation() {
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(getContext().getApplicationContext());
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(mBDLocationListener);
        mLocationClient.start();

    }
    //定位监听器
    private BDLocationListener mBDLocationListener=new BDLocationListener() {



        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation==null) {
                mLocationClient.requestLocation();
                return;
            }
            double latitude = bdLocation.getLatitude();//获取经度
            double longitude = bdLocation.getLongitude();//获取维度
            mCurrentLocation = new LatLng(latitude,longitude);//设置坐标
            mLocationAddr = bdLocation.getAddrStr();
            String currentAddr = bdLocation.getAddrStr();//获取当前地址
            Log.i("TAG","定位的位置:"+currentAddr+",经纬度:"+latitude+","+longitude);

            MyLocationData data=new MyLocationData.Builder()
                    .latitude(latitude)//
                    .longitude(longitude)
                    .accuracy(100f)
                    .build();
            //定位数据显示到地图上
            mBaiduMap.setMyLocationData(data);
            //移动到定位的地方
            moveToLocation();
            //做一次判断,第一次进入页面的时候自动移动,其他时候点击按钮移动
            if (mIsFirst) {
                moveToLocation();
                mIsFirst=false;
            }

        }
    };
    //初始化mapView
    private void initMapView() {
        MapStatus mapStatus = new MapStatus.Builder()
                .zoom(19)//地图的放大
                .overlook(0)//俯仰的角度
                .rotate(0)//旋转的调度
                .build();
        //百度地图的设置信息
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)
                .zoomGesturesEnabled(true)
                .scaleControlEnabled(false)
                .zoomControlsEnabled(false);
        mapView = new MapView(getContext(), options);
        //布局上添加地图控件
        mMapFrame.addView(mapView, 0);
        //拿到地图的操作类
        mBaiduMap = mapView.getMap();
        //地图状态监听器
        mBaiduMap.setOnMapStatusChangeListener(mStatusChangeListener);

        mBaiduMap.setOnMarkerClickListener(mMarkerClickListener);
    }
    //设置mapView监听器
    private BaiduMap.OnMarkerClickListener mMarkerClickListener=new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //判断当前标记物是否为空
            if(mCurrentMarker!=null){
                //判断当前标记物和上一个标记物是否为同一标记物
                if(mCurrentMarker!=marker){
                    //隐藏上一个标记物
                    mCurrentMarker.setVisible(true);
                }
                mCurrentMarker.setVisible(true);
            }
            //将选中标记物设置为当前标记物
            mCurrentMarker = marker;
            mCurrentMarker.setVisible(false);
            InfoWindow infoWindow=new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    if(mCurrentMarker!=null){
                       //切换普通视图
                        changeUIMode(UI_MODE_NORMAL);
                    }
                    mBaiduMap.hideInfoWindow();
                }
            });
            mBaiduMap.showInfoWindow(infoWindow);

            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            mTreasureView.bindTreasure(treasure);

            //切换宝藏视图
            changeUIMode(UI_MODE_SECLECT);
            return false;
        }
    };
    //地图状态监听方法
    private BaiduMap.OnMapStatusChangeListener mStatusChangeListener=new BaiduMap.OnMapStatusChangeListener() {
        //变化前
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }
        //变化中
        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }
        //变化后
        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            //当前地图的位置
            LatLng target=mapStatus.target;
            if (target!=MapFragment.this.mCurrentStatua) {
                //地图状态发生变化以后实时的获取当前区域内的宝藏
                urdataMapArea();
                //判断是不是埋藏宝藏的情况下
                if (mUIMode==UI_MODE_HIDE) {
                    ReverseGeoCodeOption option=new ReverseGeoCodeOption();
                    //设置反地理编码的位置信息
                    option.location(target);
                    //发起反地理编码
                    mGeoCoder.reverseGeoCode(option);
                }

                MapFragment.this.mCurrentStatua=target;
            }
        }
    };




    //处理卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType(){
        int mapType = mBaiduMap.getMapType();//获取当前的地图类型
        //切换类型
        mapType=(mapType==BaiduMap.MAP_TYPE_NORMAL)?BaiduMap.MAP_TYPE_SATELLITE :BaiduMap.MAP_TYPE_NORMAL;
        //设置切换类型时卫星和普通的文字显示
        String msg=mapType==BaiduMap.MAP_TYPE_NORMAL?"卫星":"普通";
        mBaiduMap.setMapType(mapType);
        mTvSatellite.setText(msg);

    }
    @OnClick(R.id.tv_compass)
    //显示指南针
    public void seitchCompass(){
        //指南针有没有显示
        boolean compassEnabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!compassEnabled);
    }
    //地图的缩放
    @OnClick({R.id.iv_scaleDown,R.id.iv_scaleUp})
    public void scaleMap(View view){
        switch (view.getId()) {
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
        }
    }
    //定位按钮
    @OnClick(R.id.tv_located)
    public void moveToLocation(){
        //将当前位置设置为定位的地方
        MapStatus mapStatus=new MapStatus.Builder()
                .target(mCurrentLocation)
                .rotate(0)
                .overlook(0)
                .zoom(19)
                .build();
        //更新状态
        MapStatusUpdate update=MapStatusUpdateFactory.newMapStatus(mapStatus);
        //更新展示的地图状态
        mBaiduMap.animateMapStatus(update);
    }
    //根据位置的变化,区域也发生了变化(整个手机地图界面的中心)
    private void urdataMapArea() {
        //当前地图的状态
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        //当前经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        //根据所在位置拿到一块区域
        Area area=new Area();
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLat(Math.floor(latitude));
        area.setMinLng(Math.floor(longitude));

        //根据区域获取宝藏数据
        mapPresenter.getTreasure(area);

    }

    //宝藏卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView(){
        //拿到marker的宝藏信息,跳转页面
        int id = mCurrentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(),treasure);
    }
    //点击宝藏标题卡片跳转宝藏详情页面
    @OnClick(R.id.hide_treasure)
    public void hideTreasure(){
        String title = etTreasureTitle.getText().toString();
        if(TextUtils.isEmpty(title)){
            mActivityUtils.showToast("请输入宝藏标题");
            return;
        }
        LatLng latLng=mBaiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(),title,mCurrentAddr,latLng,0);
    }

    private BitmapDescriptor dot= BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand= BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    private void addMarker(LatLng latLng,int treasureId) {
        MarkerOptions options=new MarkerOptions();
        options.position(latLng);//覆盖物位置
        options.icon(dot);//覆盖物坐标
        options.anchor(0.5f,0.5f);//居中
        //将宝藏ID信息保存到marker
        Bundle bundle=new Bundle();
        bundle.putInt("id",treasureId);
        options.extraInfo(bundle);
        //添加覆盖物
        mBaiduMap.addOverlay(options);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public static LatLng getMyLocation(){
        return mCurrentLocation;

    }
    public  void changeUIMode(int uiMode){
        if (mUIMode==uiMode) return;
            mUIMode=uiMode;

            switch (uiMode){
                //普通的视图
            case UI_MODE_NORMAL:
                if (mCurrentMarker!=null) {
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mLayoutBottom.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
            break;
            //宝藏选中视图
            case UI_MODE_SECLECT:
                mLayoutBottom.setVisibility(View.VISIBLE);
                mTreasureView.setVisibility(View.VISIBLE);
                mCenterLayout.setVisibility(View.GONE);
                mHideTreasure.setVisibility(View.GONE);
            break;
            //宝藏埋藏视图
            case UI_MODE_HIDE:
                if (mCurrentMarker!=null) {
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mCenterLayout.setVisibility(View.VISIBLE);
                mLayoutBottom.setVisibility(View.GONE);
                mBtnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLayoutBottom.setVisibility(View.VISIBLE);
                        mTreasureView.setVisibility(View.GONE);
                        mHideTreasure.setVisibility(View.VISIBLE);
                    }
                });
            break;
        }

    }
    public static String getLocationAddr(){
        return mLocationAddr;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    //-------------------数据请求的视图方法------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setData(List<Treasure> list) {
        mBaiduMap.clear();
        for (Treasure treasure:list) {
            Log.i("TAG", "Data: "+treasure.getId()+","+treasure.getLatitude()+","+treasure.getLongitude());
            //拿到集合中的宝藏的经纬度
            LatLng latLng=new LatLng( treasure.getLatitude(),treasure.getLongitude());
            //添加覆盖物
            addMarker(latLng,treasure.getId());

        }
    }
    public boolean clickbackPrssed(){
        //不是普通视图,切换成普通视图
        if(mUIMode!=UI_MODE_NORMAL){
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        //通知HomeActivity可以退出了
        return true;
    }
}
