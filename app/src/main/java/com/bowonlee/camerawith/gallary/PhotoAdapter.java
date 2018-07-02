package com.bowonlee.camerawith.gallary;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bowonlee.camerawith.R;
import com.bowonlee.camerawith.models.Photo;


import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>
        implements LoaderManager.LoaderCallbacks<List<Photo>>,PhotoHolder.OnItemClickListener {


    private Activity activity;
    private List<Photo> photos;
    private String mCurrentAlbum;

    public PhotoAdapter(Activity activity,String albumName){
        this.activity = activity;
        mCurrentAlbum = albumName;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallary_photo_holder,parent,false);
        PhotoHolder holder =  new PhotoHolder(view);
        holder.setOnItemClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
        holder.setPhoto(photos.get(position));
    }

    @Override
    public int getItemCount() {
        return (photos != null)? photos.size() : 0;
    }

    @Override
    public Loader<List<Photo>> onCreateLoader(int id, Bundle args) {
        return new PhotoLoader(activity,mCurrentAlbum);
    }

    @Override
    public void onLoadFinished(Loader<List<Photo>> loader, List<Photo> photos) {
        this.photos = new ArrayList<>(photos);
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Photo>> loader) {
        photos.clear();
        notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClick(Photo photo) {

        Intent intent = activity.getIntent();
        intent.putExtra(String.valueOf(R.string.parcelable_result),photo);
        activity.setResult(Activity.RESULT_OK,intent);
        activity.finish();

    }

    public void setAlbumName(String albumName){
        mCurrentAlbum = albumName;
    }
}
