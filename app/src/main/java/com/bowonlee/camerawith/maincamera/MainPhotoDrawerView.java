package com.bowonlee.camerawith.maincamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.bowonlee.camerawith.BasePhotoDrawerView;
import com.bowonlee.camerawith.models.ModifiedPhoto;

public class MainPhotoDrawerView extends BasePhotoDrawerView implements View.OnTouchListener{



    private final int EVENT_INSIDE = 401;
    private final int EVENT_EDGE = 405;
    private final int EVENT_OUTSIDE = 408;
    private final int TOLERANCE_PINCH = 8;

    private float mFrameZoomX = 0;
    private float mFrameZoomY = 0;

    private float mDistanceRateChange = 0;
    private float mPastDistance = 0;

    private int currentEventState = EVENT_OUTSIDE;

    public MainPhotoDrawerView(Context context) { super(context); }


    private float touchPastX = 0;
    private float touchPastY = 0;

    private float mTouchDistanceX=0;
    private float mTouchDistanceY=0;

    private float frameWidth = 5f;

    private int boarderWidth = 40;


    public void movePhotoXY(float x, float y){
        mModifiedPhoto.setStartXY(new PointF(x,y));

    }

    public void setPhotoRotation(int rotate){
        mModifiedPhoto.setRotation(rotate);
        setCanvasRotate(rotate);
        this.postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {



        super.onDraw(canvas);
        try {
            drawFrame(canvas);

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    private void drawFrame(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(frameWidth);

        canvas.drawRect(mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,
                mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()-mFrameZoomX,mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()-mFrameZoomY,paint);

    }

    private void drawBorderRect(Canvas canvas){


        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(10);

        float left = mModifiedPhoto.getStartXY().x + boarderWidth;
        float top = mModifiedPhoto.getStartXY().y + boarderWidth;
        float right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth() - boarderWidth - mFrameZoomX;
        float bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight() - boarderWidth - mFrameZoomY;


        canvas.drawRect(new RectF(left,top,right,bottom),paint);
    }



    private int distinguishEvent(float touchX,float touchY){
        float startX = getRotateRectWidthPivot(getPhotoRect()).left;
        float startY = getRotateRectWidthPivot(getPhotoRect()).top;
        float endX = getRotateRectWidthPivot(getPhotoRect()).right;
        float endY =getRotateRectWidthPivot(getPhotoRect()).bottom;

        if(     touchX>=startX&&
                touchX<endX&&
                touchY>=startY&&
                touchY<endY){

            if(     touchX>=startX+boarderWidth&&
                    touchX<endX-boarderWidth&&
                    touchY>=startY+boarderWidth&&
                    touchY<endY-boarderWidth) {
                return EVENT_INSIDE;
            }

            return EVENT_EDGE;


        }else{
            return EVENT_OUTSIDE;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mModifiedPhoto==null){
            return false;
        }

        if(event.getPointerCount()==1) {
            switch (event.getAction()&MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {

                    currentEventState = distinguishEvent(event.getX(), event.getY());
                    touchPastX = event.getX();
                    touchPastY = event.getY();
                    if (currentEventState == EVENT_OUTSIDE) return false;


                }
                return true;
                case MotionEvent.ACTION_MOVE: {
                    if (currentEventState == EVENT_INSIDE) {
                        movePhotoEvent(event);
                    }

                }
                return true;
                case MotionEvent.ACTION_UP: {
                    currentEventState = EVENT_OUTSIDE;
                    return false;
                }
            }
            return false;
        }else{

            float touchX1 = event.getX(0);
            float touchY1 = event.getY(0);
            float touchX2 = event.getX(1);
            float touchY2 = event.getY(1);

            if(distinguishEvent(touchX1,touchY1)==EVENT_OUTSIDE||
                    distinguishEvent(touchX2,touchY2)==EVENT_OUTSIDE){ return false; }

            switch (event.getAction()&MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_POINTER_DOWN : {
                        mPastDistance = (float) Math.sqrt(Math.pow(Math.abs(touchX1-touchX2),2)+Math.pow(Math.abs(touchY1-touchY2),2));
                        return true;
                    }
                case MotionEvent.ACTION_MOVE :{

                    mDistanceRateChange = mPastDistance - (float) Math.sqrt(Math.pow(Math.abs(touchX1-touchX2),2)+Math.pow(Math.abs(touchY1-touchY2),2));
                    mPastDistance = (float) Math.sqrt(Math.pow(Math.abs(touchX1-touchX2),2)+Math.pow(Math.abs(touchY1-touchY2),2));

                    if(Math.abs(mDistanceRateChange)>TOLERANCE_PINCH) {
                        float ratio = ((float) mPhotoBitmap.getWidth() - mDistanceRateChange / 2) / mModifiedPhoto.getOutSize().getWidth();
                        if(ratio < 1.0&&calculateMaxSize(ratio)){ mModifiedPhoto.setRatio(ratio);}
                        this.postInvalidate();

                    }

                    return true;
                }
                case MotionEvent.ACTION_POINTER_UP : {
                    currentEventState = EVENT_OUTSIDE;
                    return false;
                }
            }
            return false;
        }


    }

    private void movePhotoEvent(MotionEvent event){

        mTouchDistanceX = touchPastX - event.getX();
        mTouchDistanceY = touchPastY - event.getY();

        touchPastX = event.getX();
        touchPastY = event.getY();

        movePhotoXY((int)( mModifiedPhoto.getStartXY().x - mTouchDistanceX),(int)( mModifiedPhoto.getStartXY().y - mTouchDistanceY));

        this.postInvalidate();
    }
    @Override
    public ModifiedPhoto getModifiedPhoto() {
       return super.getModifiedPhoto();
    }


    private boolean calculateMaxSize(float ratio){

        if((float)mModifiedPhoto.getOutSize().getWidth() * ratio > getContext().getResources().getDisplayMetrics().widthPixels
                ||(float)mModifiedPhoto.getOutSize().getHeight() * ratio > getContext().getResources().getDisplayMetrics().heightPixels){
            return false;
        }else{ return true;}
    }
}
