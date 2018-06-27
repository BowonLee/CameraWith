package com.bowonlee.camerawith.resultpreview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.widget.Toast;

import com.bowonlee.camerawith.FileIOHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class BitmapSaver implements Runnable{

    private File mFile;
    private Context mContext;

    private FileIOHelper mFileIOHelper;
    private String mFileName;
    private Bitmap bitmap;
    private int mOrientation;

    public BitmapSaver(Bitmap bitmap, Context context,int orientation){
        this.bitmap = bitmap;
        mContext = context;
        this.mOrientation = orientation;
    }

    @Override
    public void run() {


        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmss");
        String getTime = sdf.format(date);
        mFileName = "dpf"+getTime +".jpg";


        mFileIOHelper = new FileIOHelper();
        mFile = new File(mFileIOHelper.getAlbumStorageDir("CameraWith"),mFileName);

        FileOutputStream output = null;


        Matrix tempMatrix = new Matrix();
        tempMatrix.setRotate(-1 * mOrientation);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),tempMatrix,false);

        try {
            output = new FileOutputStream(mFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG,100,output);


            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        MediaScannerConnection.scanFile(mContext, new String[]{mFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                }
        );

        Toast.makeText(mContext,String.format("Complete Save File : %s",mFile.toURI().getPath()),Toast.LENGTH_SHORT).show();
    }
}
