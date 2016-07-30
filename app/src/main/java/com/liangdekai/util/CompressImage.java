package com.liangdekai.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CompressImage {

    public static int calculateInSampleSize(BitmapFactory.Options options , int requireHeigh , int requireWidth){
        final int heigh = options.outHeight ;
        final int width = options.outWidth ;
        int inSampleSize = 1 ;
        if (heigh > requireHeigh || width > requireWidth){
            final int heighRatio = (Math.round((float)heigh / (float) requireHeigh));
            final int widthRatio = (Math.round((float)width / (float) requireWidth));
            inSampleSize = heighRatio > widthRatio ? widthRatio : heighRatio ;
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
