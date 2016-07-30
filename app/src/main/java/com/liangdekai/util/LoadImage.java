package com.liangdekai.util;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.liangdekai.activity.ShowActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class LoadImage {
    public static final int TASK_COME = 0;
    public static final int THREAD_COUNT = 3;
    public static final int COMPRESS_SIZE = 200;
    private static LoadImage mLoadImage;
    private Handler mThreadHandler;
    private volatile Semaphore semaphore ;
    private Handler mHandler ;
    private HandlerThread mTaskThread;
    private LruCache<String , Bitmap> mLruCache ;
    private ExecutorService mFixedThreadPool;
    private LinkedList<Runnable> mTaskList ;

    public static LoadImage getInstance(){
        if (mLoadImage == null){
            synchronized (LoadImage.class){
                if (mLoadImage == null){
                    mLoadImage = new LoadImage();
                }
            }
        }
        return mLoadImage;
    }

    public LoadImage(){
        init();
    }

    private void init(){
        mTaskThread = new HandlerThread("handlerThread");
        mTaskThread.start();
        mThreadHandler = new Handler(mTaskThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                mFixedThreadPool.execute(getLoadTask());
            }
        };

        int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        int cacheMemory = maxMemory / 6 ;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mFixedThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        mTaskList = new LinkedList<Runnable>();
        semaphore = new Semaphore(THREAD_COUNT);
    }

    private Bitmap getFromLruCache(String key){
        return mLruCache.get(key);
    }

    private void addToLruCache(String key , Bitmap bitmap){
        if (getFromLruCache(key) == null && bitmap !=null){
            mLruCache.put(key , bitmap);
        }
    }

    private Runnable getLoadTask(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTaskList.removeLast();
    }

    private void addLoadTask(Runnable loadTask){
        mTaskList.add(loadTask);
        mThreadHandler.sendEmptyMessage(TASK_COME);
    }

    public void loadImage(final String path , final ImageView imageView){
        Holder holder = new Holder();
        mHandler = new Handler(Looper.getMainLooper()){
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
        final Bitmap bitmap= getFromLruCache(path);
        if (bitmap != null){
            holder.bitmap = bitmap;
            holder.imageView = imageView;
            holder.path = path ;
            Message message = Message.obtain();
            message.obj = holder ;
            mHandler.sendMessage(message);
        }else{
            addLoadTask(new Runnable() {
                @Override
                public void run() {
                    Holder holder = new Holder();
                    Bitmap bm = CompressImage.compressImage(path , COMPRESS_SIZE , COMPRESS_SIZE);
                    addToLruCache(path , bm);
                    holder.bitmap = bm;
                    holder.imageView = imageView;
                    holder.path = path ;
                    Message message = Message.obtain();
                    message.obj = holder ;
                    mHandler.sendMessage(message);
                    semaphore.release();
                }
            });
        }
    }

    public void closeThreadPool(){
        mFixedThreadPool.shutdownNow();
    }

    class Holder {
        ImageView imageView ;
        Bitmap bitmap ;
        String path ;
    }
}
