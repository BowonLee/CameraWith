package com.bowonlee.camerawith.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bowon on 2018-04-19.
 */

/*
* 미디어 파일 중 사진파일을 정의하는 클레스,
* 사진파일과 해당 사진의 URI를 관리한다.
*
* Class that handle imagedata and thumnail.
* attribute : thumnailUri, imageUr  i
* */
public class Photo implements Parcelable{

    public static final String EXTRA_CODE = "PurePhoto";
    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            Uri thumnailUri = Uri.CREATOR.createFromParcel(in);
            Uri imageUri = Uri.CREATOR.createFromParcel(in);
            return new Photo(thumnailUri,imageUri);
        }

        @Override
        public Photo[] newArray(int size) {return new Photo[size];}
    };

    private Uri thumnailUri;
    private Uri imageUri;
    public Photo(Uri thumnail,Uri image) {
        thumnailUri = (thumnail == null)? image : thumnail;
        imageUri = image;
    }

    public Photo(){}
    public void setPhoto(Photo photo){
        this.imageUri = photo.imageUri;
        this.thumnailUri = photo.thumnailUri;
    }
    @Override
    public int describeContents() {return 0;}
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        thumnailUri.writeToParcel(dest,flags);
        imageUri.writeToParcel(dest,flags);
    }


    public Uri getThumnailUri(){return thumnailUri;}
    public Uri getImageUri(){return imageUri;}
}
