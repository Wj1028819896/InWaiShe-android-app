package com.inwaishe.app.framework.emotionkeyboard;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.inwaishe.app.R;
import com.inwaishe.app.framework.emotionkeyboard.model.Emotion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by WangJing on 2017/12/11.
 *
 */

public class EmojiUtils {
    
    public static EmojiUtils mInstance = null;

    private ArrayList<Emotion> mEmotions = null;

    public static synchronized EmojiUtils getInstance(Context con){
        if(mInstance == null){
            try {
                mInstance = new EmojiUtils(con);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mInstance;
    }

    private EmojiUtils(Context con) throws IOException {

        //.获取表情数据
        mEmotions = new ArrayList<>();
        mEmotions.addAll(parseXml(con.getAssets().open("e_a.xml")));
        mEmotions.addAll(parseXml(con.getAssets().open("e_b.xml")));

    }

    public void setEmojiTextView(Context context, TextView textView, String emojiStr){

        String zz = new String("\\[[^\\]\\x22]{2,100}\\]");
        String zz1 = "\\[[^\\[\\]]*\\]";
        Pattern pattern = Pattern.compile(zz1);
        Matcher matcher = pattern.matcher(emojiStr);
        SpannableString sp = new SpannableString(emojiStr);

        while(matcher.find()){
            //.获取匹配到的子字符串[xxx]
            String group = matcher.group();
            //.在该字符串开始的位置 与结束的位置
            int start = matcher.start();
            int end = matcher.end();

            for(Emotion emotion:mEmotions){
                if(group.equals(emotion.code)){
                    Drawable d = context.getResources().getDrawable(emotion.resId);
                    int textSize = (int) (textView.getTextSize()*1.5);
                    d.setBounds(0, 0, textSize, textSize);
                    sp.setSpan(new ImageSpan(d,ImageSpan.ALIGN_BOTTOM), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        textView.setText(sp);
    }

    /**
     * 解析表情数据
     * @param is
     * @return
     */
    public ArrayList<Emotion> parseXml(InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        ArrayList<Emotion> emotions = new ArrayList<>();
        try {
            // ❷Ⅱ获得DocumentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();
            // ❸Ⅲ--获得文档对象--
            Document doc = builder.parse(is);
            // ❹Ⅳ获得根元素
            Element element = doc.getDocumentElement();
            NodeList nodeList = element.getChildNodes();

            for(int i = 0; i < nodeList.getLength(); i++){
                Node emotionNode = nodeList.item(i);
                NodeList list = emotionNode.getChildNodes();
                Emotion emotion = new Emotion();
                for(int dex = 0;dex < list.getLength();dex++ ){
                    if("code".equals(list.item(dex).getNodeName())){
                        emotion.code = list.item(dex).getTextContent();
                    }else if("resid".equals(list.item(dex).getNodeName())){
                        emotion.name = list.item(dex).getTextContent();
                        Field f = (Field) R.drawable.class.getDeclaredField(emotion.name);
                        int j = f.getInt(R.drawable.class);
                        emotion.resId = j;
                    }else if("msg".equals(list.item(dex).getNodeName())){
                        emotion.msg = list.item(dex).getTextContent();
                    }else if("filepath".equals(list.item(dex).getNodeName())){
                        emotion.filepath = list.item(dex).getTextContent();
                    }else if("url".equals(list.item(dex).getNodeName())){
                        emotion.url = list.item(dex).getTextContent();
                    }
                }
                emotions.add(emotion);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return emotions;
    }
}
