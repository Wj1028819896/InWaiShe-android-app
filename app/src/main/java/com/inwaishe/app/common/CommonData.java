package com.inwaishe.app.common;

import android.os.Environment;

import java.io.File;

/**
 * Created by WangJing on 2017/10/9.
 */

public class CommonData {

    public static String SAVE_BASE_DIR = Environment.getExternalStorageDirectory() + File.separator + "INWAISHE";

    public static String SHAREVIEW_FILENAME = "SHAREVIEW_FILENAME.jpeg";

    /**访问LoginPage 获得 loginhash**/
    public static String LOGIN_PAGE = "http://www.inwaishe.com/member.php?mod=logging&action=login&infloat=yes&handlekey=login&inajax=1&ajaxtarget=fwin_content_login";

    public static String REPLY_URL = "http://www.inwaishe.com/forum.php?mod=post&action=reply&fid=41&tid=9957&extra=page=1&replysubmit=yes&infloat=yes&handlekey=fastpost&inajax=1";

    public static String FASTREPLY_URL = "http://www.inwaishe.com/forum.php?mod=post&action=reply&tid=9966&replysubmit=yes&infloat=yes&handlekey=fastpost";

    public static String UID_SPACE = "http://www.inwaishe.com/space-uid-%s.html";


    public static int SUCCESS = 0x1501;
    public static int FAIL = 0x1502;
    public static int REQUESTCODE_LOGIN = 0x2001;

    public static String RETURN_JSON = "return_json";

    public static String UID = "usr_id";

}
