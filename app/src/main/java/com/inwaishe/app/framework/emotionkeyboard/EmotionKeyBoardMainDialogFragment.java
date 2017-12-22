package com.inwaishe.app.framework.emotionkeyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.emotionkeyboard.adapter.EmotionAdapter;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;
import com.inwaishe.app.framework.emotionkeyboard.model.EmotionGroupCofig;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by WangJing on 2017/10/17.
 */

public class EmotionKeyBoardMainDialogFragment extends DialogFragment implements View.OnClickListener {

    private Dialog mDialog;
    private View mRootView;
    private EditText commentEditText;
    private ImageView photoButton;
    private ImageView atButton;
    private ImageView emotionButton;
    private TextView sendButton;
    private InputMethodManager inputMethodManager;
    private DialogFragmentDataCallback dataCallback;
    private FrameLayout mFlEmotionpad;
    private LinearLayout mLlTop;
    private View mViewTop;
    //private ArrayList<Emotion> emotionArrayList = new ArrayList<>();
    SharedPreferences sp;
    private String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "SHARE_PREFERENCE_SOFT_INPUT_HEIGHT";
    private String SHARE_PREFERENCE_NAME = "SHARE_PREFERENCE_NAME";
    private String SHARE_PREFERENCE_EMOTIONCONFIGS = "SHARE_PREFERENCE_EMOTIONCONFIGS";

    private Activity mActivity;
    private ArrayList<EmotionGroupCofig> emotionGroupCofigs = new ArrayList<>();
    @Override
    public void onAttach(Context context) {
        if (!(getActivity() instanceof DialogFragmentDataCallback)) {
            throw new IllegalStateException("DialogFragment 所在的 activity 必须实现 DialogFragmentDataCallback 接口");
        }
        mActivity = getActivity();
        sp = mActivity.getSharedPreferences(SHARE_PREFERENCE_NAME,Context.MODE_PRIVATE);
        super.onAttach(context);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL,R.style.BottomDialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        Window window = getDialog().getWindow();

        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.dialog_fragment_comment_layout,container,false);

        Bundle data = getArguments();
        String hint = data.getString("hint","");

        commentEditText = (EditText) mRootView.findViewById(R.id.edit_comment);
        photoButton = (ImageView) mRootView.findViewById(R.id.image_btn_photo);
        atButton = (ImageView) mRootView.findViewById(R.id.image_btn_at);
        sendButton = (TextView) mRootView.findViewById(R.id.txt_btn_comment_send);
        emotionButton = (ImageView) mRootView.findViewById(R.id.image_btn_myemoji);
        mFlEmotionpad = (FrameLayout) mRootView.findViewById(R.id.flemotionpad);
        mLlTop = (LinearLayout) mRootView.findViewById(R.id.LiTop);
        mViewTop = mRootView.findViewById(R.id.viewTop);


        fillEditText();
        initData();
        setSoftKeyboard();
        commentEditText.setHint(hint);
        commentEditText.addTextChangedListener(mTextWatcher);
        photoButton.setOnClickListener(this);
        atButton.setOnClickListener(this);

        sendButton.setOnClickListener(this);
        emotionButton.setOnClickListener(this);
        KeyBoardListenerUtils.getmInstance().bindonClickListener(new KeyBoardListenerUtils.onClickListener() {
            @Override
            public void addEmotion(Emotion emotion) {
                inputEmotion(commentEditText,emotion);
            }

            @Override
            public void onDelecte() {
                commentEditText.dispatchKeyEvent(new KeyEvent(
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
            }
        });
        return mRootView;
    }

    private void initData() {

        //初始化表情组数据
        emotionGroupCofigs.clear();
        //默认表情组添加Emoji
        EmotionGroupCofig defaultCof = new EmotionGroupCofig();
        defaultCof.assetfile = "e_a.xml";
        defaultCof.resId = R.drawable.e_a_0;
        emotionGroupCofigs.add(defaultCof);
        //默认表情组添加数码
        EmotionGroupCofig defaultCof1 = new EmotionGroupCofig();
        defaultCof1.assetfile = "e_b.xml";
        defaultCof1.resId = R.drawable.e_b_0;
        emotionGroupCofigs.add(defaultCof1);
        //添加用户下载表情组（从SharePrefence 得到）
        String configs = sp.getString(SHARE_PREFERENCE_EMOTIONCONFIGS,"");
        if(!TextUtils.isEmpty(configs)){
            String[] paths = configs.split(";");
            for(String path:paths){
                EmotionGroupCofig usrCof = new EmotionGroupCofig();
                usrCof.filepath = "" + path;
                emotionGroupCofigs.add(usrCof);
            }
        }
    }



    /**
     * 输入表情
     * @param etInput
     * @param emotion
     */
    private void inputEmotion(EditText etInput,Emotion emotion) {
        int cursor = etInput.getSelectionStart();
        try {
            Drawable d = getResources().getDrawable(emotion.resId);
            int textSize = (int) (etInput.getTextSize()*1.5);
            d.setBounds(0, 0, textSize, textSize);
            String str = emotion.code;
            SpannableString ss = new SpannableString(str);
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
            ss.setSpan(span, 0, str.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            etInput.getText().insert(cursor, ss);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fillEditText() {
        dataCallback = (DialogFragmentDataCallback) getActivity();
        commentEditText.setText(dataCallback.getCommentText());
        commentEditText.setSelection(dataCallback.getCommentText().length());
        if (dataCallback.getCommentText().length() == 0) {
            sendButton.setEnabled(false);
            sendButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.iconCover));
        }
    }

    private void setSoftKeyboard() {
        commentEditText.setFocusable(true);
        commentEditText.setFocusableInTouchMode(true);
        commentEditText.requestFocus();
        commentEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mFlEmotionpad.isShown()) {
                    lockTopViewHeight();
                    showSoftInputKeyBoard();
                    hideEmotionKeyBoardLayout(150);
                    unlockTopViewHeight();
                }
                return false;
            }
        });

        // TODO: 17-8-11 为何这里要延时才能弹出软键盘, 延时时长又如何判断？ 目前是手动调试
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 110);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 0) {
                sendButton.setEnabled(true);
                sendButton.setClickable(true);
                sendButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            } else {
                sendButton.setEnabled(false);
                sendButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.iconCover));
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_btn_photo:
                Toast.makeText(getActivity(), "Pick photo Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_btn_at:
                Toast.makeText(getActivity(), "Pick people you want to at Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.txt_btn_comment_send:
                //Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                //commentEditText.setText("");
                dismiss();
                break;
            case R.id.image_btn_myemoji:
                if(mFlEmotionpad.isShown()){
                    lockTopViewHeight();
                    showSoftInputKeyBoard();
                    hideEmotionKeyBoardLayout(150);
                    unlockTopViewHeight();
                }else{
                    if(isSoftInputKeyBoardShown()){
                        lockTopViewHeight();
                        showEmotionKeyBoardLayout();
                        unlockTopViewHeight();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dataCallback.setCommentText(commentEditText.getText().toString());
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dataCallback.setCommentText(commentEditText.getText().toString());
        super.onCancel(dialog);
    }

    public interface DialogFragmentDataCallback {

        String getCommentText();

        void setCommentText(String commentTextTemp);
    }

    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight(Activity mActivity) {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;
        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
        }
        //存一份到本地
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        Log.i("软键盘","软键盘高度->" + softInputHeight);
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 锁定TopView 高度
     */
    private  void lockTopViewHeight(){
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mViewTop.getLayoutParams();
        params1.weight = 0.0F;
        params1.height = mViewTop.getHeight();
        //mViewTop.setLayoutParams(params1);
    }


    /**
     * 解锁TopView高度
     */
    private void unlockTopViewHeight(){
        commentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                //((LinearLayout.LayoutParams) mViewTop.getLayoutParams()).height = 0;
                ((LinearLayout.LayoutParams) mViewTop.getLayoutParams()).weight = 1.0F;
            }
        },400L);
    }

    /**
     * 显示表情键盘
     */
    private void showEmotionKeyBoardLayout(){
        int softInputHeight = getSupportSoftInputHeight(mActivity);
        if(softInputHeight == 0){
            softInputHeight = sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT
                    ,mActivity.getWindow().getDecorView().getRootView().getHeight()/2);
        }
        hideSoftInputKeyBoard(200L);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFlEmotionpad.getLayoutParams();
        //params.weight = 0;
        params.height = softInputHeight;
        //mFlEmotionpad.setLayoutParams(params);
        mFlEmotionpad.setVisibility(View.VISIBLE);
        commentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                showEmotionData();
            }
        },300L);
    }

    private void showEmotionData() {

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        EmotionbarFragment emotionbarFragment = EmotionbarFragment.newInstance(emotionGroupCofigs);
        ft.replace(R.id.flemotionpad, emotionbarFragment);
        ft.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFlEmotionpad.isShown()){
        }
    }

    /**
     * 隐藏表情键盘
     * @param delay 延迟时间
     */
    private void hideEmotionKeyBoardLayout(int delay){
        commentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFlEmotionpad.isShown()) {
                    mFlEmotionpad.setVisibility(View.GONE);
                }
            }
        },delay);
    }

    /**
     * 是否显示软件盘
     * @return
     */
    private boolean isSoftInputKeyBoardShown() {
        return getSupportSoftInputHeight(mActivity) != 0;
    }

    /**
     * 显示软键盘
     */
    private void showSoftInputKeyBoard(){
        commentEditText.requestFocus();
        commentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputManager.showSoftInput(commentEditText, 0);
            }
        }, 0);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInputKeyBoard(long delay) {
        commentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager mInputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputManager.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
            }
        },delay);
    }
}
