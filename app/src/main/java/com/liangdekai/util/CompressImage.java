package com.liangdekai.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompressImage {

    public static int calculateInSampleSize(BitmapFactory.Options options , int requireHeigh , int requireWidth){
        final int height = options.outHeight ;
        final int width = options.outWidth ;
        int inSampleSize = 1 ;
        if (height > requireHeigh || width > requireWidth){
            final int heightRatio = (Math.round((float)height / (float) requireHeigh));
            final int widthRatio = (Math.round((float)width / (float) requireWidth));
            inSampleSize = heightRatio > widthRatio ? widthRatio : heightRatio ;
        }
        return inSampleSize;
    }

    public static Bitmap compressImage(String path, int requireHeigh , int requireWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true ;
        BitmapFactory.decodeFile(path ,options);
        options.inSampleSize = calculateInSampleSize(options , requireHeigh , requireWidth);
        options.inJustDecodeBounds = false ;
        return BitmapFactory.decodeFile(path , options);
    }
}
