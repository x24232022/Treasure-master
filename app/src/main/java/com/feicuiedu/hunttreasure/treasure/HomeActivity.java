package com.feicuiedu.hunttreasure.treasure;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.feicuiedu.hunttreasure.MainActivity;
import com.feicuiedu.hunttreasure.R;
import com.feicuiedu.hunttreasure.commons.ActivityUtils;
import com.feicuiedu.hunttreasure.treasure.list.TreasureListFragment;
import com.feicuiedu.hunttreasure.treasure.map.MapFragment;
import com.feicuiedu.hunttreasure.user.UserPrefs;
import com.feicuiedu.hunttreasure.user.account.AccountActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navgation)
    NavigationView mNavgationView;
    @BindView(R.id.drawerlayout)
    DrawerLayout mDrawerlayout;
    private ImageView mIvIcon;
    private ActivityUtils mActivityUtils;
    private MapFragment mapFragment;
    private TreasureListFragment mListFragment;
    private FragmentManager mSupportFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mSupportFragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) mSupportFragmentManager.findFragmentById(R.id.mapFragment);
        mActivityUtils = new ActivityUtils(this);
        TreasureRepo.getInstance().clear();
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerlayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        mDrawerlayout.addDrawerListener(toggle);

        mNavgationView.setNavigationItemSelectedListener(this);
        //设置头像点击
        mIvIcon = (ImageView) mNavgationView.getHeaderView(0).findViewById(R.id.iv_usericon);
        mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivityUtils.startActivity(AccountActivity.class);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
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

    //设置菜单点击
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hide:
                //切换埋藏宝藏视图
                mapFragment.changeUIMode(2);
                break;
            case R.id.menu_logout:
                UserPrefs.getInstance().clearUser();
                mActivityUtils.startActivity(MainActivity.class);
                finish();
                break;

        }
        mDrawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //准备选项菜单
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //当前的选项菜单中ID为这个的菜单项
        MenuItem item = menu.findItem(R.id.action_toggle);
        //根据显示的视图不一样设置不一样的图标
        if (mListFragment!=null&&mListFragment.isAdded()) {
            item.setIcon(R.drawable.ic_map);
        }else {
            item.setIcon(R.drawable.ic_view_list);
        }

        return true;
    }

    //创建选项菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return true;
    }

    //选择某一个选项菜单项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle:
                showListFragment();
                //更新选项菜单的视图,调用会触发onPrepareOptionsMenu()方法
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    //显示或隐藏列表的视图
    public void showListFragment() {

        if (mListFragment != null && mListFragment.isAdded()) {
            mSupportFragmentManager.popBackStack();
            mSupportFragmentManager.beginTransaction()
                    .remove(mListFragment).commit();
            return;

        }
        mListFragment=new TreasureListFragment();
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,mListFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerlayout.closeDrawer(GravityCompat.START);
        } else {
            //MapFragment是普通的视图模式
            if (mapFragment.clickbackPrssed()) {
                super.onBackPressed();
            }

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(mIvIcon);
    }
}
