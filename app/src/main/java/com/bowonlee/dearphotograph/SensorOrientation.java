package com.bowonlee.dearphotograph;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;

public class SensorOrientation {

    private final int ORIENTATION_PORTRAIT = ExifInterface.ORIENTATION_ROTATE_90;
    private final int ORIENTATION_LANDSCAPE_REVERSE = ExifInterface.ORIENTATION_ROTATE_180;
    private final int ORIENTATION_LANDSCAPE = ExifInterface.ORIENTATION_NORMAL;
    private final int ORIENTATION_PORTRAIT_REVERSE = ExifInterface.ORIENTATION_ROTATE_270;

    int smoothness = 1;
    private float averagePitch = 0;
    private float averageRoll = 0;
    private int orientation = ORIENTATION_PORTRAIT;

    private float[] pitches;
    private float[] rolls;

    interface OrientationChangeListener{
        public int OnOrientationChanged();
    }

    private OrientationChangeListener listener;


    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] mGravity;
        float[] mGeomagnetic;

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity = event.values;
            }
            if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD){
                mGeomagnetic = event.values;
            }
            if(mGravity != null && mGeomagnetic != null){
                float R[] = new float[9];
                float I[] = new float[9];
                if(SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic)){
                    float orientationData[] = new float[3];
                    SensorManager.getOrientation(R,orientationData);
                    averagePitch = addValue(orientationData[1],pitches);
                    averageRoll = addValue(orientationData[2],rolls);
                    orientation = calculateOrientation();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public SensorOrientation(){
        pitches = new float[smoothness];
        rolls = new float[smoothness];
    }

    public SensorEventListener getEventListener(){

        return sensorEventListener;
    }



    public void setOnOrientationListener(OrientationChangeListener listener){
        this.listener = listener;
    }

    public int getOrientation() {
        return orientation;
    }


    private float addValue(float value, float[] values){
        float average = 0;
        value = (float)Math.round(Math.toDegrees(value));

        for (int i= 1;i<smoothness;i++){
            values[i -1] = values[i];
            average += values[i];
        }
        values[smoothness -1] = value;
        average = (average + value) / smoothness;
        return average;

    }

    private int calculateOrientation(){
        if((orientation == ORIENTATION_PORTRAIT||orientation == ORIENTATION_PORTRAIT_REVERSE
                &&(averageRoll>-30&&averageRoll<30))){
            if (averagePitch>0){
                return ORIENTATION_PORTRAIT_REVERSE;
            }else{
                return ORIENTATION_PORTRAIT;
            }
        }else{
            if(Math.abs(averagePitch)>=30){
                if(averagePitch>0){
                    return ORIENTATION_PORTRAIT_REVERSE;
                }else{
                    return ORIENTATION_PORTRAIT;
                }
            }else{
                if(averageRoll>0){
                    return ORIENTATION_LANDSCAPE_REVERSE;
                }else{
                    return ORIENTATION_LANDSCAPE;
                }
            }
        }

    }
}
