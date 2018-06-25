package com.bowonlee.camerawith.resultpreview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bowonlee.camerawith.models.ModifiedPhoto;
import com.bowonlee.camerawith.R;

import java.util.ArrayList;

public class PreviewResultFragment extends Fragment {

    public final int MAX_OUTPUT_WIDTH = 3120;
    public final int MAX_OUTPUT_HEIGHT = 4160;


    public interface PreviewResultInterface{


        public void onFinishPreviewResult(ModifiedPhoto photo);

    }

    private PreviewResultInterface mPreviewResultInterface;

    private final String TAG = "PreviewResultFragment";

    /*UI*/
    private ArrayList<ImageButton> mButtonGruop;
    private ImageButton mButtonSaveImage;
    private ImageButton mButtonCancel;

    private PreviewResultView mPreviewResultView;
    private Bitmap mCapturedBitmap;
    private RelativeLayout mParentLayout;
    private ModifiedPhoto mModifiedPhoto;

    private int mOrientation;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreviewResultView = new PreviewResultView(getActivity(),mCapturedBitmap);

        mParentLayout = (RelativeLayout)view.findViewById(R.id.layout_preview_result);


        mModifiedPhoto.setStartXY(new PointF(
                mModifiedPhoto.getStartXY().x,
                (float)( ((mModifiedPhoto.getStartXY().y -  (float)( (float)getResources().getDisplayMetrics().widthPixels*16.0/9.0 - (float)mCapturedBitmap.getHeight())/2.0)
                        )

                )
        ));

        mPreviewResultView.setPhoto(mModifiedPhoto);
        mParentLayout.addView(mPreviewResultView);

        mButtonSaveImage = (ImageButton)view.findViewById(R.id.btn_fragment_preview_result_save);
        mButtonCancel = (ImageButton)view.findViewById(R.id.btn_fragment_preview_result_cancel);

        settingButtons();


        mPreviewResultView.postInvalidate();


    }

    @Override
    public void onResume() {
        super.onResume();
        mButtonSaveImage.bringToFront();
        mButtonCancel.bringToFront();

    }
    public void setModifiedPhoto(ModifiedPhoto modifiedPhoto){
        this.mModifiedPhoto = new ModifiedPhoto(modifiedPhoto);
    }

    public void setOrientation(int orientation){
        this.mOrientation = orientation;
    }

    public void setCapturedBitmap(Bitmap capturedBitmap){
        this.mCapturedBitmap = capturedBitmap;
    }
    private void settingButtons(){

        mButtonGruop = new ArrayList<>();
        mButtonGruop.add(mButtonSaveImage);
        mButtonGruop.add(mButtonCancel);

        for(ImageButton btn : mButtonGruop){
            mParentLayout.bringChildToFront(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_preview_result_cancel :{
                            mPreviewResultInterface.onFinishPreviewResult(mModifiedPhoto);
                            onDestroy();}break;
                        case R.id.btn_fragment_preview_result_save : {
                            saveView();
                        }break;
                    }
                }
            });
        }

    }

    private void saveView(){

        ModifiedPhoto tempphoto = new ModifiedPhoto(mModifiedPhoto);

        float outputAspectRatio = (float) mCapturedBitmap.getWidth()/(float)mCapturedBitmap.getHeight();

        float expendRatio = (float) tempphoto.getOutSize().getWidth()*tempphoto.getRatio()/(float) getResources().getDisplayMetrics().widthPixels;

        float outputWidth =  1080;

        float resizeRatio =  (outputWidth/(float) getResources().getDisplayMetrics().widthPixels);



        tempphoto.setRatio((tempphoto.getRatio()*resizeRatio));
        tempphoto.setStartXY(new PointF(mModifiedPhoto.getStartXY().x*resizeRatio,mModifiedPhoto.getStartXY().y*resizeRatio));

        PreviewResultView tempview = new PreviewResultView(getActivity(),
                Bitmap.createScaledBitmap(mCapturedBitmap,(int) outputWidth,(int) (outputWidth/outputAspectRatio),false),
                tempphoto);

        tempview.setPhoto(tempphoto);
        tempview.setPhotoRotation(tempphoto.getRotation());
        tempview.postInvalidate();


        Bitmap b = Bitmap.createBitmap((int) outputWidth,(int) (outputWidth/outputAspectRatio),Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        tempview.draw(c);
        new BitmapSaver(b,getActivity(),mOrientation).run();

        mPreviewResultView = null;

        mPreviewResultInterface.onFinishPreviewResult(mModifiedPhoto);

        }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_preview_result,container,false);
    }
    public void setPreviewResultInterface(PreviewResultInterface previewResultInterface){
        this.mPreviewResultInterface = previewResultInterface;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }


}
