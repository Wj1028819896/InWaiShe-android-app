package com.inwaishe.app.framework.webviewstyleselector.widget;

/**
 * Created by WangJing on 2017/12/15.
 *
 */

public class BuilderConfig {

    //刻度文字列表
    String[] scaleWords;
    //刻度文字颜色列表
    int[] scaleWordColors;
    //刻度文字大小
    int[] scaleWordSp;
    //刻度标志的颜色
    int[] scaleMarksColor;
    //选择回调
    NumSeekBar.selecterListener selecterListener;
    //开始时刻度位置
    int beginScalePosition = 0;


    private NumSeekBar numSeekBar;
    public BuilderConfig(NumSeekBar numSeekBar){
        this.numSeekBar = numSeekBar;
    }

    public BuilderConfig setScaleWords(String[] scaleWords) {
        this.scaleWords = scaleWords;
        return this;
    }

    public BuilderConfig setScaleWordColors(int[] scaleWordColors) {
        this.scaleWordColors = scaleWordColors;
        return this;
    }

    public BuilderConfig setScaleWordSp(int[] scaleWordSp) {
        this.scaleWordSp = scaleWordSp;
        return this;
    }

    public BuilderConfig setScaleMarksColor(int[] scaleMarksColor) {
        this.scaleMarksColor = scaleMarksColor;
        return this;
    }

    public BuilderConfig setSelecterListener(NumSeekBar.selecterListener selecterListener) {
        this.selecterListener = selecterListener;
        return this;
    }

    public BuilderConfig setBeginScalePosition(int beginScalePosition) {
        this.beginScalePosition = beginScalePosition;
        return this;
    }

    public void Build(){
        this.numSeekBar.build(this);
    }
}
