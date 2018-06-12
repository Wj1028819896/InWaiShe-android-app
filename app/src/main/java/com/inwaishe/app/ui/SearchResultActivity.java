package com.inwaishe.app.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.SearchResListAdapter;
import com.inwaishe.app.base.BaseActivity;
import com.inwaishe.app.entity.SearchResBackInfo;
import com.inwaishe.app.viewmodel.SearchListViewModel;

public class SearchResultActivity extends BaseActivity implements LifecycleRegistryOwner{

    private EditText mEtInputSearch;
    private ImageView mIvClearInput;
    private TextView mTvCancel;
    private RecyclerView mRcList;
    private TextView vNoFind;
    private View vFindding;
    private SearchListViewModel mSearchListViewModel;
    private SearchResListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mEtInputSearch = (EditText) findViewById(R.id.etSearchInput);
        mIvClearInput = (ImageView) findViewById(R.id.ivClearTxt);
        mTvCancel = (TextView) findViewById(R.id.tvCancel);
        mRcList = (RecyclerView) findViewById(R.id.rcSearchResList);
        vFindding = findViewById(R.id.vFindding);
        vNoFind = (TextView) findViewById(R.id.vNofind);
    }

    private void initData() {
        mSearchListViewModel =  ViewModelProviders.of(this).get(SearchListViewModel.class);
        mSearchListViewModel.getResBackInfoMutableLiveData().observe(this, new Observer<SearchResBackInfo>() {
            @Override
            public void onChanged(@Nullable SearchResBackInfo searchResBackInfo) {
                if(mAdapter != null){
                    mAdapter.setLoadingMore(false);
                    mAdapter.setLoadAll(searchResBackInfo.isLoadAll);
                    mAdapter.notifyDataSetChanged();
                    vFindding.setVisibility(View.GONE);
                    if(searchResBackInfo.code < 0){
                        if(searchResBackInfo.code == -2){
                            vNoFind.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(SearchResultActivity.this,"" + searchResBackInfo.msg,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mAdapter = new SearchResListAdapter(mRcList,this,mSearchListViewModel.getResBackInfoMutableLiveData().getValue());
        mAdapter.setOnLoadOrRefreshListener(new SearchResListAdapter.OnLoadOrRefreshListener() {
            @Override
            public void onLoadMore() {
                Log.e("SearchListViewModel","onLoadMore");
                String key = "" + mEtInputSearch.getText().toString();
                if(TextUtils.isEmpty(key)){
                    return;
                }
                mSearchListViewModel.loadData(false,key);
            }

            @Override
            public void onRefresh() {
                String key = "" + mEtInputSearch.getText().toString();
                if(TextUtils.isEmpty(key)){
                    return;
                }
                mSearchListViewModel.loadData(true,key);
            }
        });
        mRcList.setAdapter(mAdapter);

    }

    private void initEvent() {
        mEtInputSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    doSearch();
                }
                return false;
            }
        });

        mEtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    mIvClearInput.setVisibility(View.VISIBLE);
                }else{
                    mIvClearInput.setVisibility(View.GONE);
                }
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvClearInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInputSearch.setText("");
            }
        });

    }

    private void doSearch() {
        Log.e("SearchListViewModel","doSearch");
        vNoFind.setVisibility(View.GONE);
        String key = "" + mEtInputSearch.getText().toString();
        if(TextUtils.isEmpty(key)){
            Toast.makeText(this,"请输入搜索内容",Toast.LENGTH_LONG).show();
            return;
        }
        vFindding.setVisibility(View.VISIBLE);
        InputMethodManager mInputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputManager.hideSoftInputFromWindow(mEtInputSearch.getWindowToken(), 0);
        mSearchListViewModel.loadData(true,key);
    }

    LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
