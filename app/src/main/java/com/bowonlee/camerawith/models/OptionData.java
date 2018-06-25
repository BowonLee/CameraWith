package com.bowonlee.camerawith.models;

import android.content.Context;
import android.content.SharedPreferences;

public class OptionData {

    private final String prefKey = "settingData";

    public static final String KEY_ASPECT_RATIO = "aspectRatio";
    public static final String KEY_WHITE_BALANCE = "whiteBalance";
    public static final String KEY_TIMER_SET = "timerSet";
    public static final String KEY_CAMERA_FACING = "cameraFacing";
    public static final String KEY_TIMER_ACTIVATE = "tImerOn";
    public static final String KEY_FLASH_STATE = "flashState";


    static public final int ASPECT_RATIO_9_16 = 1;
    static public final int ASPECT_RATIO_3_4 = 0;
    static public final int ASPECT_RATIO_1_1 = 2;

    static public final int WHITEBALANCE_AUTO = 0;
    static public final int WHITEBALANCE_COLUDY = 1;
    static public final int WHITEBALANCE_DAYLIGHT = 2;
    static public final int WHITEBALANCE_FLUORSCENT = 3;
    static public final int WHITEBALANCE_INCANDSCENT = 4;

    static public final int TIMER_SEC_3 = 0;
    static public final int TIMER_SEC_5 = 1;
    static public final int TIMER_SEC_10 = 2;

    static public final int FLASH_AUTO      = 0 ;
    static public final int FLASH_ON        = 1 ;
    static public final int FLASH_OFF       = 2 ;

    static public final int CAMERA_FACING_BACK = 0;
    static public final int CAMERA_FACING_FRONT = 1;

    static public final int TIMER_OFF = 0;
    static public final int TIMER_ON = 1;


    private SharedPreferences mPreference;
    private SharedPreferences.Editor mEditor;

    public OptionData(Context context){

        mPreference = context.getSharedPreferences(prefKey,Context.MODE_PRIVATE);
        mEditor = mPreference.edit();
    }

    public void setData(String key,int value){

        mEditor.putInt(key, value);
        mEditor.commit();

    }

    public int getSingleData(String key){
        return mPreference.getInt(key,0);
    }












}
