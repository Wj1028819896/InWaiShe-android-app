package com.inwaishe.app.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.ui.fragment.MainPagerFragment;
import com.inwaishe.app.ui.fragment.NewsPagerFragment;
import com.inwaishe.app.ui.fragment.VedioPagerFragment;

public class MainActivity extends AppCompatActivity {


    LazyFragment mMainPagerFragemnt;
    LazyFragment mNewsPagerFragment;
    LazyFragment mVedioPagerFragment;
    LazyFragment mCenterPagerFragment;

    FrameLayout mFmContainer;

    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            mMainPagerFragemnt = (LazyFragment) getSupportFragmentManager().findFragmentByTag(MainPagerFragment.TAG);
            mNewsPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag(NewsPagerFragment.TAG);
            mCenterPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag("");
            mVedioPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag(VedioPagerFragment.TAG);
        }
        initView();
        initData();
        initEvent();

        seclectFragmentPagerByTag(MainPagerFragment.TAG);
    }



    private void initView() {
        mFmContainer = (FrameLayout) findViewById(R.id.fmContainer);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
    }

    private void initData() {

    }

    private void initEvent() {

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener(){

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int menuId = menuItem.getItemId();
                        switch (menuId){
                            case R.id.bottom_menu_home:
                                seclectFragmentPagerByTag(MainPagerFragment.TAG);
                                break;
                            case R.id.bottom_menu_evaluate:
                                seclectFragmentPagerByTag(NewsPagerFragment.TAG);
                                break;
                            case R.id.bottom_menu_vedio:
                                seclectFragmentPagerByTag(VedioPagerFragment.TAG);
                                break;
                            case R.id.bottom_menu_center:
                                break;
                        }
                        return true;
                    }
                });

    }

    private void seclectFragmentPagerByTag(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        hideFragmentPagers(ft);
        switch (tag){
            case MainPagerFragment.TAG:
                if(mMainPagerFragemnt == null){
                    mMainPagerFragemnt = new MainPagerFragment();
                    ft.add(mFmContainer.getId(),mMainPagerFragemnt,MainPagerFragment.TAG);
                }else{
                    ft.show(mMainPagerFragemnt);
                }
                break;
            case NewsPagerFragment.TAG:

                if(mNewsPagerFragment == null){
                    mNewsPagerFragment = new NewsPagerFragment();
                    ft.add(mFmContainer.getId(),mNewsPagerFragment,NewsPagerFragment.TAG);
                }else{
                    ft.show(mNewsPagerFragment);
                }
                break;
            case VedioPagerFragment.TAG:
                if(mVedioPagerFragment == null){
                    mVedioPagerFragment = new VedioPagerFragment();
                    ft.add(mFmContainer.getId(),mVedioPagerFragment,NewsPagerFragment.TAG);
                }else{
                    ft.show(mVedioPagerFragment);
                }
                break;
        }
        ft.commit();
    }

    private void hideFragmentPagers(FragmentTransaction ft){
        if(mMainPagerFragemnt != null){
            ft.hide(mMainPagerFragemnt);
        }
        if(mNewsPagerFragment != null){
            ft.hide(mNewsPagerFragment);
        }
        if(mCenterPagerFragment != null){
            ft.hide(mCenterPagerFragment);
        }
        if(mVedioPagerFragment != null){
            ft.hide(mVedioPagerFragment);
        }
    }
}
