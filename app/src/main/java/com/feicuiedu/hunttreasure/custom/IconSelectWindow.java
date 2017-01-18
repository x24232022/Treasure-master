package com.feicuiedu.hunttreasure.custom;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.feicuiedu.hunttreasure.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class IconSelectWindow extends PopupWindow {
    //接口回调的方式实现布局中相册相机两个按钮的事件
    public interface Listener{
        void toGallery();
        void toCamera();
    }
    private Activity mActivity;

    private Listener mListener;
    public IconSelectWindow(@NonNull Activity activity,Listener listener) {
        super(activity.getLayoutInflater().inflate(R.layout.window_select_icon, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ButterKnife.bind(this,getContentView());

        mActivity = activity;
        mListener=listener;

        setFocusable(true);
        //一定要设置背景
        setBackgroundDrawable(new BitmapDrawable());

    }

    //对外提供一个展示的方法
    public void show() {
        showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    @OnClick({R.id.btn_gallery, R.id.btn_camera, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gallery:
                mListener.toGallery();
                break;
            case R.id.btn_camera:
                mListener.toCamera();
                break;
            case R.id.btn_cancel:

                break;
        }
        dismiss();
    }
}
