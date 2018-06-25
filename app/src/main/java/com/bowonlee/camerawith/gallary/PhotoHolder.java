package com.bowonlee.camerawith.gallary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bowonlee.camerawith.R;
import com.bowonlee.camerawith.models.Photo;


public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    interface OnItemClickListener{
        void onItemClick(Photo photo);
    }



    private ImageView photoView;
    private Photo photo;
    private OnItemClickListener listener;

    public PhotoHolder(View view) {
        super(view);

        photoView= (ImageView)view.findViewById(R.id.photoholder_image);

        view.setOnClickListener(this);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public void setPhoto(Photo photo){
        this.photo = photo;
        photoView.setImageURI(photo.getThumnailUri());
    }

    @Override
    public void onClick(View v) {

        listener.onItemClick(photo);


    }
}
