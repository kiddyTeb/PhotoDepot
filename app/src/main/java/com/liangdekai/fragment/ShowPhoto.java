package com.liangdekai.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liangdekai.photodepot.R;

public class ShowPhoto extends Fragment{
    private String mPath;
    private ImageView mImageView;
    private View mView ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_show , container , false);
        init();
        Bundle bundle = getArguments();
        if (bundle != null){
            mPath = bundle.getString("path");
        }
        return mView;
    }

    private void init(){
        mImageView = (ImageView) mView.findViewById(R.id.fragment_show);
    }

    public static ShowPhoto getInstance(String path){
        ShowPhoto showPhoto = new ShowPhoto() ;
        Bundle bundle = new Bundle();
        bundle.putString("path",path);
        showPhoto.setArguments(bundle);
        return showPhoto;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);
        Log.d("test",mPath);
        if (bitmap == null){
            Log.d("test","null");
        }
        mImageView.setImageBitmap(bitmap);
    }
}
