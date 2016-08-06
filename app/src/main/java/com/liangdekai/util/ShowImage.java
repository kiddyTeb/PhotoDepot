package com.liangdekai.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.liangdekai.photodepot.R;

/**
 * 该类用于设置图片的显示
 */

public class ShowImage {
    private static final int COMPRESS_SIZE = 100;
    private static final int COMPRESS_BIG_SIZE = 800;
    private static ShowImage mShowImage ;
    private TaskManager mTaskManager;
    private CacheManager mCacheManager ;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Holder dataHolder = (Holder) msg.obj;
            ImageView image = dataHolder.imageView;
            String imagePath = dataHolder.path;
            Bitmap imageBitmap = dataHolder.bitmap;
            if (image.getTag().toString().equals(imagePath)){
                image.setImageBitmap(imageBitmap);
            }
        }
    };

    /**
     * 获取该类的实例
     * @return
     */
    public static ShowImage getInstance(){
        if (mShowImage == null){
            synchronized (ShowImage.class){
                if (mShowImage == null){
                    mShowImage = new ShowImage();
                }
            }
        }
        return mShowImage;
    }

    public ShowImage(){
        mTaskManager = TaskManager.getInstance() ;
        mCacheManager = CacheManager.getInstance() ;
    }

    /**
     * 为每个Item设置图片
     * @param path
     * @param imageView
     */
    public void loadImage(final String path , final ImageView imageView){
        Holder holder = new Holder();
        final Bitmap bitmap= mCacheManager.getFromLruCache(path);
        if (bitmap != null){
            holder.bitmap = bitmap;
            holder.imageView = imageView;
            holder.path = path ;
            Message message = Message.obtain();
            message.obj = holder ;
            mHandler.sendMessage(message);
        }else{
            mTaskManager.addLoadTask(new Runnable() {
                @Override
                public void run() {//任务详情
                    Holder holder = new Holder();
                    Bitmap bm = CompressImage.compressImage(path , COMPRESS_SIZE , COMPRESS_SIZE);//压缩图片
                    mCacheManager.addToLruCache(path , bm);//添加到缓存
                    holder.bitmap = bm;
                    holder.imageView = imageView;
                    holder.path = path ;
                    Message message = Message.obtain();
                    message.obj = holder ;
                    mHandler.sendMessage(message);
                    mTaskManager.semaphore.release();//释放信号量
                }
            });
        }
    }

    /**
     * 加载压缩的大图
     * @param path
     * @param imageView
     */
    public void loadLargeImage(String path ,ImageView imageView){
        Bitmap bitmap= mCacheManager.getFromLruCache(path);
        if (imageView != null){
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else {
                imageView.setImageResource(R.mipmap.empty);
                Bitmap bm = CompressImage.compressImage(path , COMPRESS_SIZE , COMPRESS_SIZE);//压缩图片
                mCacheManager.addToLruCache(path , bm);
            }
        }
    }

    /**
     * 加载原图
     * @param path
     * @param imageView
     */
    public void loadImageDetail(final String path , final ImageView imageView){
        mTaskManager.addLoadTask(new Runnable() {
            @Override
            public void run() {
                Holder holder = new Holder();
                holder.bitmap = CompressImage.compressImage(path , COMPRESS_BIG_SIZE , COMPRESS_BIG_SIZE);
                holder.imageView = imageView;
                holder.path = path ;
                Message message = Message.obtain();
                message.obj = holder ;
                mHandler.sendMessage(message);
                mTaskManager.semaphore.release();//释放信号量
            }
        });
    }

    class Holder {
        ImageView imageView ;
        Bitmap bitmap ;
        String path ;
    }
}
