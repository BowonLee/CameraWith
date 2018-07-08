package com.bowonlee.camerawith;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProgressLoading extends LinearLayout{

    private TextView mTextviewPregressText;

    public ProgressLoading(Context context) {
        super(context);
        addView(inflate(context, R.layout.progress_loading,null)
                , new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

        mTextviewPregressText = (TextView)findViewById(R.id.textview_progressLoading_text);
        this.setVisibility(GONE);

    }

    public void setProgressText(String progressText){
        mTextviewPregressText.setText(progressText);
    }

    public void startProgress(){

        this.setVisibility(VISIBLE);
    }
    public void endProgress(){
        this.setVisibility(GONE);
    }


}

