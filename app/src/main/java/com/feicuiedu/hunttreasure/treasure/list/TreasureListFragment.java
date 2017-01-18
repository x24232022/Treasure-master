package com.feicuiedu.hunttreasure.treasure.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.treasure.TreasureRepo;

/**
 * Created by Administrator on 2017/1/12 0012.
 */

public class TreasureListFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //创建RecyclerView
        mRecyclerView = new RecyclerView(container.getContext());
        //设置布局管理器

        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置动画的效果
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置背景
        mRecyclerView.setBackgroundResource(R.mipmap.screen_bg);


        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置适配器
        TreasureListAdapter adapter=new TreasureListAdapter();
        mRecyclerView.setAdapter(adapter);
        adapter.addItemData(TreasureRepo.getInstance().getTreasure());
    }
}
