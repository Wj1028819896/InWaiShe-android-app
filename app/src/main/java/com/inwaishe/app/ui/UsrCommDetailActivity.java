package com.inwaishe.app.ui;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.adapter.CommonDetailAdapter;
import com.inwaishe.app.common.DialogUtils;
import com.inwaishe.app.dataprovider.DataProvider;
import com.inwaishe.app.entity.mainpage.ArcCommInfo;
import com.inwaishe.app.entity.mainpage.baseResponse;
import com.inwaishe.app.framework.arch.bus.XBus;
import com.inwaishe.app.framework.arch.bus.XBusObserver;
import com.inwaishe.app.framework.arch.bus.XBusThreadModel;
import com.inwaishe.app.framework.emotionkeyboard.EmotionKeyBoardMainDialogFragment;
import com.inwaishe.app.viewmodel.CommDetailViewModel;

public class UsrCommDetailActivity extends AppCompatActivity implements LifecycleRegistryOwner,EmotionKeyBoardMainDialogFragment.DialogFragmentDataCallback{

    private ArcCommInfo info;
    private CommDetailViewModel commDetailViewModel;
    private RecyclerView recyclerView;
    private CommonDetailAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private Button mBtnClose;
    private TextView mBtnAddReply;
    public static String EVENT_REPLY_SUCCESS = "EVENT_REPLY_SUCCESS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usr_comm_detail);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.rvCommList);
        mBtnClose = (Button) findViewById(R.id.btnClose);
        mBtnAddReply = (TextView) findViewById(R.id.edit_comment);
        mBtnAddReply.setText("回复楼主...");
    }

    private void initData() {
        info = (ArcCommInfo) getIntent().getSerializableExtra("ArcCommInfo");
        commDetailViewModel = ViewModelProviders.of(this).get(CommDetailViewModel.class);
        commDetailViewModel.init(info);
        commDetailViewModel.getArcCommInfoMutableLiveData().observe(this, new Observer<ArcCommInfo>() {
            @Override
            public void onChanged(@Nullable ArcCommInfo arcCommInfo) {
                refreshView();
            }
        });
        mAdapter = new CommonDetailAdapter(this,commDetailViewModel.getArcCommInfoMutableLiveData().getValue());
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        commDetailViewModel.loadPageDataForArcComm();
    }

    private void initEvent() {
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnAddReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataProvider.isNeedLogin(UsrCommDetailActivity.this)){
                    return;
                }
                actionTemp = commDetailViewModel.getArcCommInfoMutableLiveData().getValue().replyAction;
                EmotionKeyBoardMainDialogFragment emotionKeyBoardMainDialogFragment = new EmotionKeyBoardMainDialogFragment();
                Bundle data = new Bundle();
                data.putString("hint","回复楼主...");
                emotionKeyBoardMainDialogFragment.setArguments(data);
                emotionKeyBoardMainDialogFragment.show(getSupportFragmentManager(), "CommentDialogFragment");
            }
        });

        XBus.getInstance().observe(this,"ReplyChildClick",xReplyChildClickObserver).runOn(XBusThreadModel.mainThread());
    }

    /**
     * 回复地址
     */
    String actionTemp = "";
    /**
     * 观察者
     */
    XBusObserver xObserver = new XBusObserver<baseResponse>() {
        @Override
        public void onCall(baseResponse var) {
            DialogUtils.close();
            if(var.code > 0 && var.html.contains("非常感谢，回复发布成功")){
                Toast.makeText(UsrCommDetailActivity.this,"非常感谢，回复发布成功",Toast.LENGTH_SHORT).show();
                //刷新本页
                commDetailViewModel.loadPageDataForArcComm();
                //刷新上一页
                XBus.getInstance().post(EVENT_REPLY_SUCCESS,"refresh");
            }else{
                Toast.makeText(UsrCommDetailActivity.this,"" + var.msg,Toast.LENGTH_SHORT).show();
            }
        }
    };
    /**
     * 点击子评论回复
     */
    XBusObserver xReplyChildClickObserver = new XBusObserver<String>() {
        @Override
        public void onCall(String action) {
            if(DataProvider.isNeedLogin(UsrCommDetailActivity.this)){
                return;
            }
            actionTemp = action.split("<<>>")[1];
            String hint = action.split("<<>>")[0];
            EmotionKeyBoardMainDialogFragment emotionKeyBoardMainDialogFragment = new EmotionKeyBoardMainDialogFragment();
            Bundle data = new Bundle();
            data.putString("hint","@"+ hint + "...");
            emotionKeyBoardMainDialogFragment.setArguments(data);
            emotionKeyBoardMainDialogFragment.show(getSupportFragmentManager(), "CommentDialogFragment");
        }
    };

    protected void refreshView(){
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);
    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }

    @Override
    public String getCommentText() {
        return "";
    }

    @Override
    public void setCommentText(String commentTextTemp) {
        if(TextUtils.isEmpty(commentTextTemp)){
            return;
        }else if(commentTextTemp.length() < 10){
            Toast.makeText(this,"回复不能少于10个字符请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtils.showProgressDialog(this,"正在回复..");
        commDetailViewModel.replyToHost(actionTemp,commentTextTemp,UsrCommDetailActivity.this,xObserver);
    }
}
