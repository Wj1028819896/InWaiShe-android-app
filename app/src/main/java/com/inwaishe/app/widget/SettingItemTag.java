package com.inwaishe.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inwaishe.app.R;
import com.inwaishe.app.ui.MyApplication;

/**
 * Created by WangJing on 2017/9/18.
 * 感谢 https://github.com/leonHua/LSettingView
 * 参考 LSetingView
 *
 * 为了解决Activity recreacte() 问题, 将布局中 的 @+id 替换为 ：Tag。
 *
 */

public class SettingItemTag extends RelativeLayout{


    /*Context*/
    private Context mContext;
    /*左侧显示文本*/
    private String mLeftText;
    /*左侧图标*/
    private Drawable mLeftIcon;
    /*右侧图标*/
    private Drawable mRightIcon;
    /*左侧显示文本大小*/
    private int mTextSize;
    /*左侧显示文本颜色*/
    private int mTextColor;
    /*右侧显示文本大小*/
    private float mRightTextSize;
    /*右侧显示文本颜色*/
    private int mRightTextColor;
    /*整体根布局view*/
    private View mView;
    /*根布局*/
    private LinearLayout mRootLayout;
    /*左侧文本控件*/
    private TextView mTvLeftText;
    /*右侧文本控件*/
    private TextView mTvRightText;
    /*左侧图标控件*/
    private ImageView mIvLeftIcon;
    /*左侧图标大小*/
    private int mLeftIconSzie;
    /*右侧图标控件区域,默认展示图标*/
    private FrameLayout mRightLayout;
    /*右侧图标控件,默认展示图标*/
    private ImageView mIvRightIcon;
    /*右侧图标控件,选择样式图标*/
    private AppCompatCheckBox mRightIcon_check;
    /*右侧图标控件,开关样式图标*/
    private SwitchCompat mRightIcon_switch;
    /*右侧图标展示风格*/
    private int mRightStyle = 0;
    /*选中状态*/
    private boolean mChecked;
    /*点击事件*/
    private OnLSettingItemClick mOnLSettingItemClick;

    public SettingItemTag(Context context) {
        this(context, null);
    }

    public SettingItemTag(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemTag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        getCustomStyle(context, attrs);
        //获取到右侧展示风格，进行样式切换
        switchRightStyle(mRightStyle);
        mRootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOn();
            }
        });
        mRightIcon_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mOnLSettingItemClick!=null){
                    mOnLSettingItemClick.click(isChecked);
                }
            }
        });

        mRightIcon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mOnLSettingItemClick!=null){
                    mOnLSettingItemClick.click(isChecked);
                }
            }
        });
    }

    public void setmOnLSettingItemClick(OnLSettingItemClick mOnLSettingItemClick) {
        this.mOnLSettingItemClick = mOnLSettingItemClick;
    }

    /**
     * 初始化自定义属性
     *
     * @param context
     * @param attrs
     */
    public void getCustomStyle(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingItem);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SettingItem_leftText) {
                mLeftText = a.getString(attr);
                mTvLeftText.setText(mLeftText);
            } else if (attr == R.styleable.SettingItem_leftIcon) {
                // 左侧图标
                try{
                    mLeftIcon = a.getResources().getDrawable(a.getResourceId(attr,0));
                }catch (Exception e){
                    e.printStackTrace();
                    mLeftIcon = VectorDrawableCompat
                            .create(getResources(),
                                    a.getResourceId(attr,0),
                                    mContext.getTheme());
                }
                if (null != mLeftIcon) {
                    mIvLeftIcon.setImageDrawable(mLeftIcon);
                    mIvLeftIcon.setVisibility(VISIBLE);
                }

            } else if (attr == R.styleable.SettingItem_leftIconSize) {
                mLeftIconSzie = (int) a.getDimension(attr, 16);
                LayoutParams layoutParams = (LayoutParams) mIvLeftIcon.getLayoutParams();
                layoutParams.width = mLeftIconSzie;
                layoutParams.height = mLeftIconSzie;
                mIvLeftIcon.setLayoutParams(layoutParams);
            } else if (attr == R.styleable.SettingItem_leftTextMarginLeft) {
                int leftMargin = (int) a.getDimension(attr, 8);
                LayoutParams layoutParams = (LayoutParams) mTvLeftText.getLayoutParams();
                layoutParams.leftMargin = leftMargin;
                mTvLeftText.setLayoutParams(layoutParams);
            } else if (attr == R.styleable.SettingItem_rightIcon) {
                // 右侧图标
                try{
                    mRightIcon = a.getResources().getDrawable(a.getResourceId(attr,0));
                }catch (Exception e){
                    e.printStackTrace();
                    mRightIcon = VectorDrawableCompat
                            .create(getResources(),
                                    a.getResourceId(attr,0),
                                    mContext.getTheme());
                }
                if (null != mRightIcon) {
                    mIvRightIcon.setImageDrawable(mLeftIcon);
                    mIvRightIcon.setVisibility(VISIBLE);
                }
            } else if (attr == R.styleable.SettingItem_LtextSize) {
                // 默认设置为16sp
                float textSize = a.getFloat(attr, 16);
                mTvLeftText.setTextSize(textSize);
            } else if (attr == R.styleable.SettingItem_LtextColor) {
                //文字默认灰色
                mTextColor = a.getColor(attr, Color.LTGRAY);
                mTvLeftText.setTextColor(mTextColor);
            } else if (attr == R.styleable.SettingItem_rightStyle) {
                mRightStyle = a.getInt(attr, 0);
            } else if (attr == R.styleable.SettingItem_isShowRightText) {
                //默认不显示右侧文字
                if (a.getBoolean(attr, false)) {
                    mTvRightText.setVisibility(View.VISIBLE);
                }else{
                    mTvRightText.setVisibility(View.INVISIBLE);
                }
            } else if (attr == R.styleable.SettingItem_rightText) {
                mTvRightText.setText(a.getString(attr));
            } else if (attr == R.styleable.SettingItem_rightTextSize) {

                // 默认设置为16sp
                mRightTextSize = a.getFloat(attr, 14);
                mTvRightText.setTextSize(mRightTextSize);
            } else if (attr == R.styleable.SettingItem_rightTextColor) {
                //文字默认灰色
                mRightTextColor = a.getColor(attr, Color.GRAY);
                mTvRightText.setTextColor(mRightTextColor);
            }
        }
        a.recycle();
    }

    /**
     * 根据设定切换右侧展示样式，同时更新点击事件处理方式
     *
     * @param mRightStyle
     */
    private void switchRightStyle(int mRightStyle) {
        switch (mRightStyle) {
            case 0:
                //默认展示样式，只展示一个图标
                mIvRightIcon.setVisibility(View.VISIBLE);
                mRightIcon_check.setVisibility(View.GONE);
                mRightIcon_switch.setVisibility(View.GONE);
                break;
            case 1:
                //隐藏右侧图标
                mRightLayout.setVisibility(View.INVISIBLE);
                break;
            case 2:
                //显示选择框样式
                mIvRightIcon.setVisibility(View.GONE);
                mRightIcon_check.setVisibility(View.VISIBLE);
                mRightIcon_switch.setVisibility(View.GONE);
                break;
            case 3:
                //显示开关切换样式
                mIvRightIcon.setVisibility(View.GONE);
                mRightIcon_check.setVisibility(View.GONE);
                mRightIcon_switch.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initView(Context context) {
        mContext = context;
        mView = View.inflate(context, R.layout.item_settingtag, this);
        mRootLayout = (LinearLayout) mView.findViewWithTag("rootLayout");
        mTvLeftText = (TextView) mView.findViewWithTag("tvSettingTitle");
        mTvRightText = (TextView) mView.findViewWithTag("tvSettingDesc");
        mIvLeftIcon = (ImageView) mView.findViewWithTag("iconLeft");
        mIvRightIcon = (ImageView) mView.findViewWithTag("ivArrowRight");
        mRightLayout = (FrameLayout) mView.findViewWithTag("flRightLayout");
        mRightIcon_check = (AppCompatCheckBox) mView.findViewWithTag("cboxSetting");
        mRightIcon_switch = (SwitchCompat) mView.findViewWithTag("rightSwitch");
    }

    /**
     * 如果不是开关模式，则处理点击事件
     * 如果是开关模式，则只更改开关状态
     */
    public void clickOn() {
        switch (mRightStyle) {
            case 0:
            case 1:
                if (null != mOnLSettingItemClick) {
                    mOnLSettingItemClick.click(mChecked);
                }
                break;
            case 2:
                //选择框切换选中状态
                mRightIcon_check.setChecked(!mRightIcon_check.isChecked());
                mChecked = mRightIcon_check.isChecked();
                break;
            case 3:
                //开关切换状态
                mRightIcon_switch.setChecked(!mRightIcon_switch.isChecked());
                mChecked = mRightIcon_check.isChecked();
                break;
        }
    }

    /**
     * 获取根布局对象
     *
     * @return
     */
    public LinearLayout getmRootLayout() {
        return mRootLayout;
    }

    /**
     * 更改左侧文字
     */
    public void setLeftText(String info) {
        mTvLeftText.setText(info);
    }

    /**
     * 更改右侧文字
     */
    public void setRightText(String info) {
        mTvRightText.setText(info);
    }

    /***
     * 更改switchButton的选中状态（）
     */
    public void setRightSwitchState(boolean isChecked){
        mRightIcon_switch.setChecked(isChecked);
        mChecked = isChecked;
    }

    /***
     * 更改CheckBox的选中状态（）
     */
    public void setRightCheckState(boolean isChecked){
        mRightIcon_check.setChecked(isChecked);
        mChecked = isChecked;
    }

    public interface OnLSettingItemClick {
        public void click(boolean isChecked);
    }

}
