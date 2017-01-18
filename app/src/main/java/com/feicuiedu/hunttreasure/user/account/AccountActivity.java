package com.feicuiedu.hunttreasure.user.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.custom.IconSelectWindow;
import com.feicuiedu.hunttreasure.user.User;
import com.feicuiedu.hunttreasure.user.UserPrefs;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import retrofit2.http.Field;

public class AccountActivity extends AppCompatActivity implements AccountView{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_userIcon)
    ImageView mIvIcon;
    private IconSelectWindow mSelectWindow;
    private ProgressDialog dialog;
    private ActivityUtils mActivityUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        mActivityUtils=new ActivityUtils(this);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()==null){
            getSupportActionBar().setTitle("个人信息");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //加载头像
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo != null) {
            Glide.with(this)
                    .load(photo)
                    .error(R.mipmap.user_icon)
                    .placeholder(R.mipmap.user_icon)//占位图
                    .dontAnimate()
                    .into(mIvIcon);
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

    @OnClick(R.id.iv_userIcon)
    public void onClick() {
        if (mSelectWindow == null) {

            mSelectWindow = new IconSelectWindow(this, listener);
        }
        if (mSelectWindow.isShowing()) {
            mSelectWindow.dismiss();
            return;
        }
        mSelectWindow.show();
    }

    private IconSelectWindow.Listener listener = new IconSelectWindow.Listener() {
        @Override
        public void toGallery() {
            //清除上一次剪切的图片缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            //到相册
            Intent intent = CropHelper.buildCropFromGalleryIntent(cropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {
            //清除上一次剪切的图片缓存
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);
            //到相机
            Intent intent = CropHelper.buildCaptureIntent(cropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };

    //图片处理
    private CropHandler cropHandler=new CropHandler() {
        //图片剪切完成后的结果
        @Override
        public void onPhotoCropped(Uri uri) {
            //拿到文件
            File file=new File(uri.getPath());
            //上传头像,更新头像

            new AccountPresenter(AccountActivity.this).uploadPhoto(file);

        }
        //剪切取消
        @Override
        public void onCropCancel() {
            Toast.makeText(AccountActivity.this, "取消", Toast.LENGTH_SHORT).show();
        }
        //剪切失败
        @Override
        public void onCropFailed(String message) {
            Toast.makeText(AccountActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
        //
        @Override
        public CropParams getCropParams() {
            CropParams cropParams=new CropParams();

            return cropParams;
        }
        //拿到上下文
        @Override
        public Activity getContext() {
            return AccountActivity.this;
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
    }
//======================================视图接口=========================
    @Override
    public void showProgress() {
        dialog = ProgressDialog.show(this, "头像上传", "正在上传中");
    }

    @Override
    public void hideProgress() {
        if (dialog!=null) {
            dialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void updataPhoto(String url) {

        if (url!=null) {
            Glide.with(this)
                    .load(url)
                    .error(R.mipmap.user_icon)
                    .dontAnimate()
                    .into(mIvIcon);
        }
    }
}
