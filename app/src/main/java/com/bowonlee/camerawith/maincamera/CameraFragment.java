package com.bowonlee.camerawith.maincamera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bowonlee.camerawith.ProgressLoading;
import com.bowonlee.camerawith.gallary.RecentPhotoLoader;
import com.bowonlee.camerawith.models.ModifiedPhoto;
import com.bowonlee.camerawith.models.OptionData;
import com.bowonlee.camerawith.models.Photo;
import com.bowonlee.camerawith.R;
import com.bowonlee.camerawith.modifier.ModifyPhotoActivity;
import com.otaliastudios.cameraview.AspectRatio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SizeSelectors;
import com.otaliastudios.cameraview.WhiteBalance;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by bowon on 2018-04-18.
 */
public class CameraFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Photo> {

    private static final String TAG = "Camera2PreviewFragment";

    private RelativeLayout mRootLayout;



    private long mTimerSet = 0;
    private long mTimerSettingValue = 3000;

    interface CameraInterface{
        void onPostTakePicture(Bitmap bitmap,ModifiedPhoto modifiedPhoto);

    }
    private CameraFragment.CameraInterface cameraInterface;


    private ArrayList<ImageButton> mButtonGroup;
    private ImageButton mButtonOpenGallary;
    private ImageButton mButtonTakePicture;
    private ImageButton mButtonRotatePicture;
    private ImageButton mButtonFlashState;
    private ImageButton mButtonMoreOption;
    private TextView mTextviewCountDown;

    private ArrayList<CheckBox> mCheckBoxGroup;
    private CheckBox mCheckBoxCameraFacing;
    private CheckBox mCheckBoxTimerActivate;

    private int mStateFlash = 0;

    private BottomSheetBehavior mBottomSheetBehavior;
    private BottomSheetOptionPanelCamera mBottomSheetOptionPanelCamera;


    private CameraView mCameraView;



    private int mPhotoRotation = 0;
    private MainPhotoDrawerView mMainPhotoDrawerView;

    private OptionData mOptionData;

    private ModifiedPhoto postPhoto;

    private ProgressLoading mProgressLoading;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootLayout = (RelativeLayout)view.findViewById(R.id.layout_camera_root);
        mCameraView = (CameraView)view.findViewById(R.id.cameraview_fragment_camera);

        setCameraView();
        setButtons(view);
        setCheckboxs(view);
        setModifiedView();
        setBottonSheet(view);
        setProgressBar(view);
        setAutoFlashButton(view);
        getLoaderManager().initLoader(0,null,this);
        setOptions(getContext());

    }
    private void setProgressBar(View view){

        mProgressLoading = new ProgressLoading(getContext());
        mRootLayout.addView(mProgressLoading,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    private void startProgressImageCapture(){
        mProgressLoading.startProgress();
        mProgressLoading.setProgressText(getString(R.string.camera_progress_save_photo));
        disableAllUI();
    }
    private void finishProgressImageCapture(){
        mProgressLoading.endProgress();
        enableAllUI();
    }

    private void disableAllUI(){
        mMainPhotoDrawerView.setOnTouchListener(null);
        for(ImageButton btn : mButtonGroup){
            btn.setVisibility(View.GONE);
        }
        for(CheckBox checkBox : mCheckBoxGroup){
            checkBox.setVisibility(View.GONE);
        }
    }
    private void enableAllUI(){
        for(ImageButton btn : mButtonGroup){
            btn.setVisibility(View.VISIBLE);
        }
        for(CheckBox checkBox : mCheckBoxGroup){
            checkBox.setVisibility(View.VISIBLE);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setOptions(Context context){
        mOptionData = new OptionData(context);

        setCameraFacing(mOptionData.getSingleData(OptionData.KEY_CAMERA_FACING));
        setTimerActivate(mOptionData.getSingleData(OptionData.KEY_TIMER_ACTIVATE));
        setFlashSetting(mOptionData.getSingleData(OptionData.KEY_FLASH_STATE));

        setCameraSize(mOptionData.getSingleData(OptionData.KEY_ASPECT_RATIO));
        setTimerSceond(mOptionData.getSingleData(OptionData.KEY_TIMER_SET));
        setWhiteBalance(mOptionData.getSingleData(OptionData.KEY_WHITE_BALANCE));




    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setCameraSize(int ratioType){
        switch (ratioType){
            case OptionData.ASPECT_RATIO_9_16 : {
                mCameraView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                mCameraView.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels*16/9;
                mCameraView.setLayoutParams(mCameraView.getLayoutParams());
                mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(9,16), 0));
            }break;
            case OptionData.ASPECT_RATIO_3_4 : {
                mCameraView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                mCameraView.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels*4/3;
                mCameraView.setLayoutParams(mCameraView.getLayoutParams());
                mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(3,4), 0));
            }break;
            case OptionData.ASPECT_RATIO_1_1 : {
                mCameraView.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
                mCameraView.getLayoutParams().height = getResources().getDisplayMetrics().widthPixels;
                mCameraView.setLayoutParams(mCameraView.getLayoutParams());
                mCameraView.setPictureSize(SizeSelectors.aspectRatio(AspectRatio.of(1,1), 0));
            }break;
        }
        mBottomSheetOptionPanelCamera.applyAspectRatio(ratioType);
        mOptionData.setData(OptionData.KEY_ASPECT_RATIO,ratioType);

        if(getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            mCameraView.stop();
            mCameraView.start();
        }



    }

    private void setWhiteBalance(int whiteBalanceType){
        switch (whiteBalanceType){
            case OptionData.WHITEBALANCE_AUTO : {mCameraView.setWhiteBalance(WhiteBalance.AUTO);}break;
            case OptionData.WHITEBALANCE_COLUDY : {mCameraView.setWhiteBalance(WhiteBalance.CLOUDY);}break;
            case OptionData.WHITEBALANCE_DAYLIGHT : {mCameraView.setWhiteBalance(WhiteBalance.DAYLIGHT);}break;
            case OptionData.WHITEBALANCE_INCANDSCENT : {mCameraView.setWhiteBalance(WhiteBalance.INCANDESCENT);}break;
            case OptionData.WHITEBALANCE_FLUORSCENT : {mCameraView.setWhiteBalance(WhiteBalance.FLUORESCENT);}break;
            }
            mBottomSheetOptionPanelCamera.applyWhiteBalance(whiteBalanceType);
        mOptionData.setData(OptionData.KEY_WHITE_BALANCE,whiteBalanceType);

    }
    private void setTimerSceond(int timerSec){
        switch (timerSec){
            case OptionData.TIMER_SEC_3 : {mTimerSettingValue = 3000;}break;
            case OptionData.TIMER_SEC_5 : {mTimerSettingValue = 5000;}break;
            case OptionData.TIMER_SEC_10 : {mTimerSettingValue = 10000;}break;
        }
        mBottomSheetOptionPanelCamera.applyTimerSecond(timerSec);
        mOptionData.setData(OptionData.KEY_TIMER_SET,timerSec);

    }
    private void setBottonSheet(View view){
        LinearLayout bottomSheetLayout = (LinearLayout)view.findViewById(R.id.bottom_sheet_fragment_camera_root);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        mBottomSheetOptionPanelCamera = new BottomSheetOptionPanelCamera(getContext(), new BottomSheetOptionPanelCamera.CameraOptionCallback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void changeAspectRatio(int ratioType) { setCameraSize(ratioType);  }

            @Override
            public void changeWhiteBalance(int whiteBalacneType) { setWhiteBalance(whiteBalacneType); }

            @Override
            public void changeTimerSecond(int timerSec) { setTimerSceond(timerSec); }

        });
        bottomSheetLayout.addView(mBottomSheetOptionPanelCamera);

    }



    private void setAutoFlashButton(View view){
        mButtonFlashState = (ImageButton)view.findViewById(R.id.btn_fragment_camera_flash);

        mButtonFlashState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStateFlash = (mStateFlash+1)%3;
                setFlashSetting(mStateFlash);
            }
        });
    }
    private void setFlashSetting(int state){
        switch (state){
            case OptionData.FLASH_AUTO : { mButtonFlashState.setImageResource(R.drawable.baseline_flash_auto_white_18);mCameraView.setFlash(Flash.AUTO);}break;
            case OptionData.FLASH_ON :   {mButtonFlashState.setImageResource(R.drawable.baseline_flash_on_white_18); mCameraView.setFlash(Flash.ON); }break;
            case OptionData.FLASH_OFF :  {mButtonFlashState.setImageResource(R.drawable.baseline_flash_off_white_18); mCameraView.setFlash(Flash.OFF);}
        }
        mOptionData.setData(OptionData.KEY_FLASH_STATE,state);
    }


    private void setCheckboxs(View view){
        mCheckBoxGroup = new ArrayList<>();
        mCheckBoxCameraFacing = (CheckBox)view.findViewById(R.id.checkbox_fragment_camera_facing);
        mTextviewCountDown = (TextView)view.findViewById(R.id.textview_fragment_camera_timertext);
        mCheckBoxTimerActivate = (CheckBox)view.findViewById(R.id.checkbox_fragment_timer_activate);

        mCheckBoxGroup.add(mCheckBoxCameraFacing);
        mCheckBoxGroup.add(mCheckBoxTimerActivate);

        for(CheckBox checkBox : mCheckBoxGroup){
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switch (buttonView.getId()){
                        case R.id.checkbox_fragment_camera_facing : {
                            if(isChecked){ setCameraFacing(OptionData.CAMERA_FACING_BACK); }
                            else{ setCameraFacing(OptionData.CAMERA_FACING_FRONT); }
                        }break;
                        case R.id.checkbox_fragment_timer_activate : {
                            if(isChecked){ setTimerActivate(OptionData.TIMER_OFF); }
                            else{ setTimerActivate(OptionData.TIMER_ON); }
                        }break;
                    }
                }
            });
        }


    }
    private void setTimerActivate(int state){

        if(state == OptionData.TIMER_OFF){ mTimerSet = 0;
        mCheckBoxTimerActivate.setChecked(true);
        }else{ mTimerSet = mTimerSettingValue;
            mCheckBoxTimerActivate.setChecked(false);
        }
        mOptionData.setData(OptionData.KEY_TIMER_ACTIVATE,state);

    }

    private void setCameraFacing(int state){
        if(state == OptionData.CAMERA_FACING_BACK){ mCameraView.setFacing(Facing.BACK);
            mCheckBoxCameraFacing.setChecked(true);
        }
        else{ mCameraView.setFacing(Facing.FRONT);
            mCheckBoxCameraFacing.setChecked(false);
        }


        mOptionData.setData(OptionData.KEY_CAMERA_FACING,state);

    }

    private void setCameraView(){

        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                Bitmap result;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;

                result = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(jpeg,0,jpeg.length,options),
                        mCameraView.getHeight(),mCameraView.getWidth(),false);

                Matrix matrix = new Matrix();



                if(mCameraView.getFacing() == Facing.FRONT){
                    matrix.postRotate(270);
                    matrix.postScale(-1,1);

                }else{
                    matrix.postRotate(90);
                    if(result.getWidth()==result.getHeight()){
                        matrix.postRotate(-90);
                    }
                }
                result = Bitmap.createBitmap(result,0,0,result.getWidth(),result.getHeight(),matrix,true);

                finishProgressImageCapture();
                cameraInterface.onPostTakePicture(result, mMainPhotoDrawerView.getModifiedPhoto());


            }

        });

        mCameraView.setLayoutParams(mCameraView.getLayoutParams());

    }

    private void setModifiedView(){


            mMainPhotoDrawerView = new MainPhotoDrawerView(getActivity());
            mMainPhotoDrawerView.setOnTouchListener(mMainPhotoDrawerView);
            mRootLayout.addView(mMainPhotoDrawerView);

            if(postPhoto !=null){
                mMainPhotoDrawerView.setPhoto(postPhoto);
            }

    }

    private void setButtons(View view){
        mButtonGroup = new ArrayList<>();
        mButtonTakePicture = (ImageButton)view.findViewById(R.id.btn_fragment_camera_takepicture);
        mButtonOpenGallary= (ImageButton)view.findViewById(R.id.btn_fragment_camera_open_gallary);
        mButtonRotatePicture = (ImageButton)view.findViewById(R.id.btn_fragment_camera_rotate_picture);
        mButtonMoreOption = (ImageButton)view.findViewById(R.id.btn_fragment_camera_more_option);


        mButtonGroup.add(mButtonOpenGallary);
        mButtonGroup.add(mButtonTakePicture);
        mButtonGroup.add(mButtonRotatePicture);
        mButtonGroup.add(mButtonMoreOption);
        for(ImageButton button : mButtonGroup){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btn_fragment_camera_more_option : {oprnButtonSheetOptionArea();}break;
                        case R.id.btn_fragment_camera_open_gallary : {openGallary();}break;
                        case R.id.btn_fragment_camera_takepicture : {takePicture();}break;
                        case R.id.btn_fragment_camera_rotate_picture : {rotatePicture();};break;
                    }
                }

            });
        }

    }
    private void oprnButtonSheetOptionArea(){

        mBottomSheetBehavior.setSkipCollapsed(true);

        if(mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        else{
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }


    private void openGallary(){
        Intent intent = new Intent(getActivity(), ModifyPhotoActivity.class);
        this.startActivityForResult(intent,ModifyPhotoActivity.REQUEST_CODE);
    }
    private void takePicture(){
        if(mMainPhotoDrawerView.getModifiedPhoto() != null){

            CountDownTimer timer = new CountDownTimer(mTimerSet,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTextviewCountDown.setVisibility(View.VISIBLE);
                    mTextviewCountDown.setText(
                                String.valueOf((millisUntilFinished)/1000)
                            );

                }
                @Override
                public void onFinish() {
                    mTextviewCountDown.setVisibility(View.GONE);
                    startProgressImageCapture();
                    mCameraView.capturePicture();
                }
            };
            disableAllUI();
            timer.start();
        }else{
            Toast.makeText(getActivity(),getString(R.string.camera_not_placed_photo) , Toast.LENGTH_SHORT).show();
        }

    }





    private void rotatePicture(){
        if(mMainPhotoDrawerView.getModifiedPhoto()!=null) {
            mPhotoRotation = (mPhotoRotation + 90)%360;
            mMainPhotoDrawerView.setPhotoRotation(mPhotoRotation);

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera,container,false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();

        if(getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            mCameraView.start();
        }
        getLoaderManager().restartLoader(0,null,this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();

    }



    public static CameraFragment newInstance(){return new CameraFragment();}
    public void setOnCameraInterface(CameraInterface cameraInterface){
        this.cameraInterface = cameraInterface;
    }



    public Size getCameraPreviewSize(){
        return new Size(720,1280);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode){
            case ModifyPhotoActivity.REQUEST_CODE : {
                if(resultCode == RESULT_OK){
                    Photo photo = data.getParcelableExtra(String.valueOf(R.string.parcelable_result));
                    setmImageOnView(photo);
                }break;
            }
        }
    }


    public void setPostPhoto(ModifiedPhoto postPhoto){

        this.postPhoto = postPhoto;
    }


     private void setmImageOnView(Photo photo){

        ModifiedPhoto modifiedPhoto = new ModifiedPhoto(photo);

        modifiedPhoto.setStartXY(new PointF(100,100));
        modifiedPhoto.setOutSize(getPhotoSize(modifiedPhoto.getImageUri()));
        modifiedPhoto.setRatio((float) mMainPhotoDrawerView.getReductionRatio(modifiedPhoto.getOutSize(),getCameraPreviewSize()));


        this.mMainPhotoDrawerView.setPhoto(modifiedPhoto);

        this.mMainPhotoDrawerView.postInvalidate();



    }

    private Size getPhotoSize(Uri photoUri){
        Size result ;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeFile(photoUri.getPath(),options);
        result = new Size(options.outWidth,options.outHeight);

        return result;
    }




    @Override
    public android.support.v4.content.Loader<Photo> onCreateLoader(int id, Bundle args) {

        return new RecentPhotoLoader(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Photo> loader, Photo data) {
        if(data !=null){
        mButtonOpenGallary.setImageURI(data.getThumnailUri());}

    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Photo> loader) {

    }




}
