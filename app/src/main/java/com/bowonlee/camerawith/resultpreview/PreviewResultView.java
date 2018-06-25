package com.bowonlee.camerawith.resultpreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.bowonlee.camerawith.BasePhotoDrawerView;
import com.bowonlee.camerawith.models.ModifiedPhoto;

public class PreviewResultView extends BasePhotoDrawerView {

    private Bitmap mCapturedBitmap;


    public PreviewResultView(Context context, Bitmap capturedBitmap){
        super(context);
        this.mCapturedBitmap = capturedBitmap;
    }

    public PreviewResultView(Context context) {
        super(context);
    }

    public PreviewResultView(Context context, Bitmap scaledBitmap, ModifiedPhoto tempphoto) {
        super(context);
        this.mCapturedBitmap = scaledBitmap;
        this.mModifiedPhoto = tempphoto;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if(mCapturedBitmap!=null) {
            canvas.rotate(0);
            canvas.drawBitmap(mCapturedBitmap, 0, 0, null);
        }
        super.onDraw(canvas);



    }


    public void setPhotoRotation(int rotate){
        mModifiedPhoto.setRotation(rotate);
        setCanvasRotate(rotate);
        this.postInvalidate();
    }



}
