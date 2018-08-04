package com.bowonlee.camerawith;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class PermissionHelper {
    private Context context ;

    public static int REQUEST_PERMISSIONS = 6;
    public PermissionHelper(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestAllPermission(){
        final Activity activity = (Activity)context;

        if(!checkAllPermissions()){
            new AlertDialog.Builder(context).setMessage(R.string.request_caemra_permission)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            activity.requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSIONS);
                        }
                    }).setCancelable(false).show();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkAllPermissions(){

        if (context.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED||
                context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void dialogPermissionDenied(){
        final Activity activity = (Activity) context;
        new AlertDialog.Builder(context).setMessage(R.string.request_permission_re_needed).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        }).setCancelable(false).show();

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean setRequestPermissionsResult(){
        if(!checkAllPermissions()){
            dialogPermissionDenied();
            return false;
        }
        return true;
    }



}
