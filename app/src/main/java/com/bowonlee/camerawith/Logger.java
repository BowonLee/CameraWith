package com.bowonlee.camerawith;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
public class Logger {

    /*
    * 디버그모드에서만 작동하는 로그
    * 앱의 시작 영역에서 디버그 여부를 확인 한 후 이후의 동작을 수행 하도록 한다.
    * */

    private static boolean debuggable = false;

    public static void checkDebuggable(Context context){
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(),0);
            debuggable = (0 != (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
            if(debuggable){
                Toast.makeText(context,"Debug Mode Activate",Toast.LENGTH_SHORT).show();}
                else{
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static final void e(String tag,String message){ if(debuggable){ Log.e(tag,message); } }
    public static final void i(String tag,String message){ if(debuggable){ Log.i(tag,message); } }
    public static final void d(String tag,String message){ if(debuggable){ Log.d(tag,message); } }
    public static final void w(String tag,String message){ if(debuggable){ Log.w(tag,message); } }
    public static final void v(String tag,String message){ if(debuggable){ Log.v(tag,message); } }
}
