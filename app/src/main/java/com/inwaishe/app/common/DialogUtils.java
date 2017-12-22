package com.inwaishe.app.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by WangJing on 2017/10/23.
 */

public class DialogUtils {


    static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Context context,String msg){
        close();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    public static void close(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.cancel();
            progressDialog = null;
        }
    }
}
