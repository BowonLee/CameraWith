package com.bowonlee.camerawith;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowon on 2018-04-08.
 */

/*
*   앱 내부에서 사용할 저장소 디렉토리를 만들고 관리하는 클레스
*
* */

public class FileIOHelper {
    private String TAG = "FileStorageHelper";


    public File getAlbumStorageDir(String albumName){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),albumName);

        if(!file.exists()){
            if(!file.mkdirs()){ }
        }
        return file;
    }

}
