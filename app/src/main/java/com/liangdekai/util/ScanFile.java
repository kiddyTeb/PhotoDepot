package com.liangdekai.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 扫描手机中所有的图片
 */
public class ScanFile {

    /**
     * 接口回调
     */
    public interface ScanListener{
        void succeed(List<String> imageList);
        void failed();
    }

    /**
     * 扫描手机文件中的图片
     * @param context
     * @param listener
     */
    public static void scanImageFile(final Context context , final ScanListener listener){
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            listener.failed();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> imageList = new ArrayList<String>();
                List<File> fileList  = new ArrayList<File>();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI ;
                ContentResolver contentResolver = context.getContentResolver();
                Cursor cursor = contentResolver.query(uri , null , MediaStore.Images.Media.MIME_TYPE +"=? or "+ MediaStore.Images.Media.MIME_TYPE+"=? or "+ MediaStore.Images.Media.MIME_TYPE+"=?" ,
                        new String[]{"image/jpg","image/png" , "image/jpeg"} , MediaStore.Images.Media.DATE_MODIFIED );
                while (cursor.moveToNext()){
                    List<String> temp ;
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    File imageFile = new File(path).getParentFile();
                    temp = Arrays.asList(imageFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            return s.endsWith(".jpg")||s.endsWith(".png") || s.endsWith(".jpeg");
                        }
                    }));
                    if (fileList.contains(imageFile)){
                        continue;
                    }
                    fileList.add(imageFile);
                    for (int i = 0 ; i < temp.size() ; i++){
                        String absolutePath = imageFile.getAbsolutePath()+"/"+temp.get(i);
                        imageList.add(absolutePath);
                    }
                }
                listener.succeed(imageList);
                cursor.close();
            }
        }).start();
    }
}
