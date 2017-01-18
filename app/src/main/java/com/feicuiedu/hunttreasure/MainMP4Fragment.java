package com.feicuiedu.hunttreasure;

import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.hunttreasure.commons.ActivityUtils;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/29 0029.
 */

public class MainMP4Fragment extends Fragment implements TextureView.SurfaceTextureListener {

    private TextureView mtextureView;
    private ActivityUtils mActivityUtils;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mtextureView = new TextureView(getActivity());
        mActivityUtils=new ActivityUtils(getActivity());
        return mtextureView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //使用Texture播放视频内容需要SutfaceTexture
        mtextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            //传入准备好的视频资源
            AssetFileDescriptor openFd=getResources().getAssets().openFd("welcome.mp4");
            //做一下资源格式的转换,setDataSource方法需要传入FileDescriptor文件
            FileDescriptor fileDescriptor = openFd.getFileDescriptor();
            mediaPlayer = new MediaPlayer();
            //传入视频资源
            mediaPlayer.setDataSource(fileDescriptor,openFd.getStartOffset(),openFd.getLength());
            //设置异步播放
            mediaPlayer.prepareAsync();
            //设置播放监听,和播放参数
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Surface mySurface=new Surface(surfaceTexture);
                    mediaPlayer.setSurface(mySurface);
                    mediaPlayer.setLooping(true);
                    //视频重头开始播放
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });

        } catch (IOException e) {

            mActivityUtils.showToast("媒体文件损坏");
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
