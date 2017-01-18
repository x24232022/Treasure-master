package com.feicuiedu.hunttreasure.treasure.hide;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.baidu.mapapi.model.LatLng;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.treasure.TreasureRepo;
import com.feicuiedu.hunttreasure.user.UserPrefs;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HideTreasureActivity extends AppCompatActivity implements HideTreasureView{

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_LOCATION = "key_location";
    private static final String KEY_LATLNG = "key_latlng";
    private static final String KEY_ALTITUDE = "key_altitude";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_description)
    EditText mEtDescription;
    @BindView(R.id.hide_send)
    ImageView mHideSend;
    private ProgressDialog mDialog;
    private ActivityUtils mActivityUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_treasure);


    }

    public static void open(Context context, String title, String address, LatLng latlng, double altitude) {
        Intent intent = new Intent(context, HideTreasureActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_LOCATION, address);
        intent.putExtra(KEY_LATLNG, latlng);
        intent.putExtra(KEY_ALTITUDE, altitude);
        context.startActivity(intent);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        mActivityUtils=new ActivityUtils(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(KEY_TITLE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
    //提价按钮上传宝藏信息
    @OnClick(R.id.hide_send)
    public void onClick() {
        Intent intent=getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        String address = intent.getStringExtra(KEY_LOCATION);
        double altitude = intent.getDoubleExtra(KEY_ALTITUDE,0);
        LatLng latLng=intent.getParcelableExtra(KEY_LATLNG);
        int tokenid= UserPrefs.getInstance().getTokenid();
        String string=mEtDescription.getText().toString();
        HideTreasure hideTreasure=new HideTreasure();
        hideTreasure.setTitle(title);
        hideTreasure.setAltitude(altitude);
        hideTreasure.setDescription(string);
        hideTreasure.setLatitude(latLng.latitude);
        hideTreasure.setLongitude(latLng.longitude);
        hideTreasure.setLocation(address);
        hideTreasure.setTokenId(tokenid);
        new HideTreasurePresenter(this).hideTreasure(hideTreasure);
    }
//==========================================================
    @Override
    public void showProgress() {
        mDialog = ProgressDialog.show(this, "宝藏上传", "宝藏上传中");

    }

    @Override
    public void hodeProgress() {
        if(mDialog!=null){
            mDialog.dismiss();
        }

    }

    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void navigationToHome() {
        finish();
        //清除缓存
        TreasureRepo.getInstance().clear();
    }
}
