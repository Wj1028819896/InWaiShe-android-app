package com.inwaishe.app.ui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.base.LazyFragment;
import com.inwaishe.app.framework.statusbar.StatusBarCompat;
import com.inwaishe.app.ui.fragment.CenterPagerFragment;
import com.inwaishe.app.ui.fragment.MainPagerFragment;
import com.inwaishe.app.ui.fragment.NewsPagerFragment;
import com.inwaishe.app.ui.fragment.VedioPagerFragment;
import com.inwaishe.app.widget.BottomNavigationViewHelper;

import java.lang.reflect.Field;

public class MainActivity extends BaseActivity {


    LazyFragment mMainPagerFragemnt;
    LazyFragment mNewsPagerFragment;
    LazyFragment mVedioPagerFragment;
    LazyFragment mCenterPagerFragment;

    FrameLayout mFmContainer;

    BottomNavigationView mBottomNavigationView;
    private String currentFragemntTag = MainPagerFragment.TAG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setContentView(R.layout.activity_main);
        if(savedInstanceState != null){
            currentFragemntTag = savedInstanceState.getString("currentFragemntTag");
            if(currentFragemntTag == null){
                currentFragemntTag = MainPagerFragment.TAG;
            }
            mMainPagerFragemnt = (LazyFragment) getSupportFragmentManager().findFragmentByTag(MainPagerFragment.TAG);
            mNewsPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag(NewsPagerFragment.TAG);
            mCenterPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag(CenterPagerFragment.TAG);
            mVedioPagerFragment = (LazyFragment) getSupportFragmentManager().findFragmentByTag(VedioPagerFragment.TAG);

            if(mMainPagerFragemnt == null){
                Log.e("001","mMainPagerFragemnt is null");

            }
        }
        initView();
        initData();
        initEvent();

        seclectFragmentPagerByTag(currentFragemntTag);

    }



    private void initView() {
        mFmContainer = (FrameLayout) findViewById(R.id.fmContainer);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
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
                                seclectFragmentPagerByTag(CenterPagerFragment.TAG);
                                break;
                        }
                        return true;
                    }
                });
    }


    private void seclectFragmentPagerByTag(String tag) {
        currentFragemntTag = tag;
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
                    ft.add(mFmContainer.getId(),mVedioPagerFragment,VedioPagerFragment.TAG);
                }else{
                    ft.show(mVedioPagerFragment);
                }
                break;
            case CenterPagerFragment.TAG:
                if(mCenterPagerFragment == null){
                    mCenterPagerFragment = new CenterPagerFragment();
                    ft.add(mFmContainer.getId(),mCenterPagerFragment,CenterPagerFragment.TAG);
                }else{
                    ft.show(mCenterPagerFragment);
                }
                break;
        }
        ft.commit();
    }

    private void hideFragmentPagers(FragmentTransaction ft){
        if(mMainPagerFragemnt != null ){
            ft.hide(mMainPagerFragemnt);
        }
        if(mNewsPagerFragment != null ){
            ft.hide(mNewsPagerFragment);
        }
        if(mCenterPagerFragment != null ){
            ft.hide(mCenterPagerFragment);
        }
        if(mVedioPagerFragment != null ){
            ft.hide(mVedioPagerFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.e(MainActivity.class.getName(), "onSaveInstanceState");
        savedInstanceState.putString("currentFragemntTag", currentFragemntTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(MainActivity.class.getName(), "onRestoreInstanceState");
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
