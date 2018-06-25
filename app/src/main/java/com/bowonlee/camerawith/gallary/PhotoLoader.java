package com.bowonlee.camerawith.gallary;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.bowonlee.camerawith.models.Photo;

import java.util.ArrayList;
import java.util.List;



public class PhotoLoader extends AsyncTaskLoader<List<Photo>>{
    private List<Photo> photos;
    private ContentResolver contentResolver;

    private Uri tableUri;
    private String[] projection ;
    private String selection ;
    private String[] selectionArgs ;

    public PhotoLoader(Context context) {
        super(context);
        contentResolver = context.getContentResolver();
    }

    @Override
    public List<Photo> loadInBackground() {
        tableUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        selection = null ;// return all row
        selectionArgs = null;

        Cursor imageCursor = contentResolver.query(tableUri,projection,selection,selectionArgs, MediaStore.MediaColumns.DATE_ADDED + " desc");
        ArrayList<Photo> result = new ArrayList<>(imageCursor.getCount());
        int dataColumnIndex = imageCursor.getColumnIndex(projection[0]);
        int idColumIndex = imageCursor.getColumnIndex(projection[1]);

        if(imageCursor.moveToFirst()){
            do {

                String filePath = imageCursor.getString(dataColumnIndex);
                String imageId = imageCursor.getString(idColumIndex);

                Uri fullImageUri = Uri.parse(filePath);
                Uri thumnailUri = uriTothumnail(imageId);
                Photo photo = new Photo(thumnailUri, fullImageUri);
                result.add(photo);
            }while (imageCursor.moveToNext());
        }
        imageCursor.close();

        return result;
    }


    private  Uri uriTothumnail(String imageId){
        tableUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        projection = new String[]{MediaStore.Images.Thumbnails.DATA};
        selection = MediaStore.Images.Thumbnails.IMAGE_ID + "=?";
        selectionArgs = new String[]{imageId};

        Cursor thumnailCursor = contentResolver.query(tableUri,projection,
                selection, selectionArgs,null);
        if(thumnailCursor.moveToFirst()){
            int thumnailColumnIndex = thumnailCursor.getColumnIndex(projection[0]);

            String thumnailPath = thumnailCursor.getString(thumnailColumnIndex);
            thumnailCursor.close();
            return Uri.parse(thumnailPath);
        }else{
            MediaStore.Images.Thumbnails.getThumbnail(contentResolver,Long.parseLong(imageId),MediaStore.Images.Thumbnails.MINI_KIND,null);
            thumnailCursor.close();
            return uriTothumnail(imageId);
        }

    }

    @Override
    public void deliverResult(List<Photo> photos) {
        if(isReset()){
            if (photos!=null){

                onReleaseResources(photos);
            }
        }
        List<Photo> oldPhotos = this.photos;
        this.photos = photos;

        if (isStarted()){
                super.deliverResult(photos);
        }
        if (oldPhotos!=null){
           onReleaseResources(oldPhotos);
        }
        super.deliverResult(photos);
    }

    @Override
    protected void onStartLoading() {
        if (photos != null){
            deliverResult(photos);
        }else{
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {cancelLoad();}

    @Override
    public void onCanceled(List<Photo> photos) {
        super.onCanceled(photos);
        onReleaseResources(photos);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (photos != null){
            onReleaseResources(photos);
            photos = null;
        }
    }

    protected void onReleaseResources(List<Photo> photos){

    }
}
