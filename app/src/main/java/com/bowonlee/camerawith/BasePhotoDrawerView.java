package com.bowonlee.camerawith;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Size;
import android.view.View;

import com.bowonlee.camerawith.models.ModifiedPhoto;

public class BasePhotoDrawerView extends View{


    protected ModifiedPhoto mModifiedPhoto;
    protected Bitmap mPhotoBitmap;
    private RectF photoRect;

    private int rotateDegree = 0;

    public BasePhotoDrawerView(Context context){
        super(context);
    }
    public BasePhotoDrawerView(Context context, ModifiedPhoto photo){
        super(context);
        mModifiedPhoto = photo;

    }

    public void setPhoto(ModifiedPhoto photo){
        mModifiedPhoto = photo;
    }

    public ModifiedPhoto getModifiedPhoto(){
        return this.mModifiedPhoto;
    }

    public double getReductionRatio(Size photoSize,Size viewSize){
        double result;

        if(photoSize.getWidth()>=photoSize.getHeight()){
            result =(((double)viewSize.getWidth()/2.0)/(double)photoSize.getWidth());
       }else{
            result = (((double)viewSize.getHeight()/2.0)/(double)photoSize.getHeight());
        }
        if(result>=1.0){
            result = 1.0;
        }

        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mModifiedPhoto != null){
            Paint paint = new Paint();
            setPhotoBitmap();

            canvas.rotate(getCanvasRotate(),
                    mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()/2,mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()/2);

            canvas.drawBitmap(mPhotoBitmap,mModifiedPhoto.getStartXY().x,mModifiedPhoto.getStartXY().y,paint);
        }
    }


    protected void setCanvasRotate(int degree){
        this.rotateDegree = degree;

    }
    protected int getCanvasRotate(){return rotateDegree;}
    protected void setPhotoBitmap(){
        mPhotoBitmap = resizedBitmapFromUri(mModifiedPhoto.getImageUri(),mModifiedPhoto.getRatio());
        rotateDegree = mModifiedPhoto.getRotation();
    }

    protected RectF getPhotoRect(){
        float top,left,bottom,right;

            left = mModifiedPhoto.getStartXY().x;
            top  = mModifiedPhoto.getStartXY().y;
            right = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth();
            bottom = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight();

        photoRect = new RectF(left,top,right,bottom);

        return photoRect;
    }

    protected RectF getRotateRectWidthPivot(RectF rect){
         float pivotX = mModifiedPhoto.getStartXY().x+mPhotoBitmap.getWidth()/2;
         float pivotY = mModifiedPhoto.getStartXY().y+mPhotoBitmap.getHeight()/2;
        float left,top,right,bottom;

        double rad = Math.toRadians((rotateDegree%180));

        left = (float)((rect.left-pivotX) * Math.cos(rad) - (rect.top-pivotY)*Math.sin(rad))+pivotX;
        top = (float)((rect.left-pivotX)*Math.sin(rad) + (rect.top-pivotY)*Math.cos(rad))+pivotY;
        right = (float)((rect.right-pivotX) * Math.cos(rad) - (rect.bottom-pivotY)*Math.sin(rad))+pivotX;
        bottom = (float)((rect.right-pivotX) *Math.sin(rad) + (rect.bottom-pivotY)*Math.cos(rad))+pivotY;


        if(rotateDegree == 0||rotateDegree==180){
        }else{
            left = left - mPhotoBitmap.getHeight();
            right = right + mPhotoBitmap.getHeight();
        }

        return new RectF(left,top,right,bottom);

    }

    private Bitmap resizedBitmapFromUri(Uri res, float ratio) {

        Bitmap result;
        int width ;
        int height ;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(res.getPath(),options);

        width = (int) (options.outWidth*ratio);
        height = (int) (options.outHeight*ratio);


        options.inSampleSize = calculateInSampleSize(options, width, height);




        options.inJustDecodeBounds = false;
        result = BitmapFactory.decodeFile(res.getPath(),options);

        return  Bitmap.createScaledBitmap(result,width,height,false);


    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }




}
