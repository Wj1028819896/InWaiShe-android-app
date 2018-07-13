package com.inwaishe.app.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.ClipboardManager;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.inwaishe.app.R;
import com.inwaishe.app.ui.MyApplication;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by WangJing on 2017/10/9.
 */

public class AppUtils {


    /**
     * 获取View 上的Bitmap
     * @param view
     * @return
     */
    public static Bitmap getBitmapFrowView(View view){

        Bitmap bm = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bm);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        // Draw view to canvas
        view.draw(canvas);
        return bm;
    }

    public static boolean saveBitmap(Bitmap bitmap, String fileName) {
        File file = new File(CommonData.SAVE_BASE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        File imageFile = new File(file, fileName);
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2px(float dpValue) {
        Context context = MyApplication.con;
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void copyToClipeBoard(Context context,String msg){
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(msg);
    }

    /**
     * 分享网页到微信朋友圈
     * @param context 上下文
     * @param imageView 图片控件
     * @param webPageTitle 标题
     * @param webPageUrl url
     * @param webPageDesc 描述
     */
    public static void shareWebPageToWxCircle(Context context,View imageView
            ,String webPageTitle,String webPageUrl,String webPageDesc){

        IWXAPI api = WXAPIFactory.createWXAPI(context,"wx2192cbd952fd8dce");

        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = webPageUrl;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.title = webPageTitle;
        wxMediaMessage.description = webPageDesc;
        wxMediaMessage.mediaObject = wxWebpageObject;
        Bitmap thumbBmp = null;
        if(imageView == null){
            thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
        }else{
            thumbBmp = Bitmap.createScaledBitmap(getBitmapFrowView(imageView), 200, 200, true);
        }
        wxMediaMessage.setThumbImage(thumbBmp);
        thumbBmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webPage" + System.currentTimeMillis();
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);

    }

    public static void shareWxImageToWxFriends(Context context){
        IWXAPI api = WXAPIFactory.createWXAPI(context,"wx2192cbd952fd8dce");
        WXImageObject wxImageObject = new WXImageObject();

    }

    /**
     * 分享网页到微信朋友
     * @param context 上下文
     * @param imageView 图片控件
     * @param webPageTitle 标题
     * @param webPageUrl url
     * @param webPageDesc 描述
     */
    public static void shareWebPageToWxFriends(Context context,View imageView
            ,String webPageTitle,String webPageUrl,String webPageDesc){

        IWXAPI api = WXAPIFactory.createWXAPI(context,"wx2192cbd952fd8dce");
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl = webPageUrl;
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.title = webPageTitle;
        wxMediaMessage.description = webPageDesc;
        wxMediaMessage.mediaObject = wxWebpageObject;
        Bitmap thumbBmp = null;
        if(imageView == null){
            thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
        }else{
            thumbBmp = Bitmap.createScaledBitmap(getBitmapFrowView(imageView), 200, 200, true);
        }
        wxMediaMessage.setThumbImage(thumbBmp);
        thumbBmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webPage" + System.currentTimeMillis();
        req.message = wxMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }


    public static void setStatusBarLightModel(Activity context, boolean isLightModel){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            return;
        }
        int visibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if(!isLightModel){
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        context.getWindow()
                .getDecorView()
                .setSystemUiVisibility(visibility);

    }
}
