package com.inwaishe.app.framework.emotionkeyboard;

import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;

/**
 * Created by WangJing on 2017/12/8.
 *
 */

public class KeyBoardListenerUtils {

    private static KeyBoardListenerUtils mInstance = null;
    private onClickListener onClickListener;
    public static KeyBoardListenerUtils getmInstance(){
        if(mInstance == null){
            mInstance = new KeyBoardListenerUtils();
        }
        return mInstance;
    }
    private KeyBoardListenerUtils(){
    }

    /**
     * 绑定监听器
     * @param onClickListener
     */
    public void bindonClickListener(onClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    interface onClickListener{
        void addEmotion(Emotion emotion);
        void onDelecte();
    }

    /**
     * 回调
     * @param emotion
     */
    public void call(Emotion emotion){
        if(this.onClickListener != null){
            if("[del]".equals(emotion.code)){
                this.onClickListener.onDelecte();
            }else{
                this.onClickListener.addEmotion(emotion);
            }
        }
    }
}
