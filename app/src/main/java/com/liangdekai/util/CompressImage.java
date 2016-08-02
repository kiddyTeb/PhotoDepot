package com.liangdekai.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 计算并且压缩图片的工具类
 */
public class CompressImage {

    /**
     * 计算采样率
     * @param options
     * @param requireHeight
     * @param requireWidth
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options , int requireHeight , int requireWidth){
        final int height = options.outHeight ;
        final int width = options.outWidth ;
        int inSampleSize = 1 ;
        if (height > requireHeight || width > requireWidth){
            final int heightRatio = (Math.round((float)height / (float) requireHeight));
            final int widthRatio = (Math.round((float)width / (float) requireWidth));
            inSampleSize = heightRatio > widthRatio ? widthRatio : heightRatio ;
        }
        return inSampleSize;
    }

    /**
     * 根据采样率对图片进行压缩
     * @param path
     * @param requireHeight
     * @param requireWidth
     * @return
     */
    public static Bitmap compressImage(String path, int requireHeight , int requireWidth){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true ;
        BitmapFactory.decodeFile(path ,options);
        options.inSampleSize = calculateInSampleSize(options , requireHeight , requireWidth);
        options.inJustDecodeBounds = false ;
        return BitmapFactory.decodeFile(path , options);
    }
}
