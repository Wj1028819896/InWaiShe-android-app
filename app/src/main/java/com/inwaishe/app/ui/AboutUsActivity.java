package com.inwaishe.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.webviewstyleselector.widget.NumSeekBar;

public class AboutUsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        NumSeekBar seekBar = (NumSeekBar) findViewById(R.id.seekbar);

        seekBar.getConfig()
                .setBeginScalePosition(2)
                .setScaleWords(new String[]{"小","中","大","最大"})
                .setScaleWordColors(new int[]{Color.GREEN,Color.YELLOW,Color.LTGRAY,Color.RED})
                .setScaleMarksColor(new int[]{Color.GREEN,Color.YELLOW,Color.LTGRAY,Color.RED})
                .setScaleWordSp(new int[]{5,10,15,20})
                .setSelecterListener(new NumSeekBar.selecterListener() {
                    @Override
                    public void onSelected(int position, String scaleWord) {
                        Toast.makeText(AboutUsActivity.this,"selected " + position + " : " + scaleWord,Toast.LENGTH_LONG).show();
                    }
                }).Build();
    }
}
