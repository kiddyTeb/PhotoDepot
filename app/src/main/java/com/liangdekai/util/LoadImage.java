package com.liangdekai.util;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载，设置缓存的工具类
 */
public class LoadImage {
    public static final int TASK_COME = 0;
    public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors()+1;
    public static final int COMPRESS_SIZE = 100;
    private static LoadImage mLoadImage;
    private Handler mThreadHandler;
    private volatile Semaphore semaphore ;
    private Handler mHandler ;
    private LruCache<String , Bitmap> mLruCache ;
    private ExecutorService mFixedThreadPool;
    private LinkedList<Runnable> mTaskList ;

    /**
     * 获取该类的实例
     * @return
     */
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

    /**
     * 构造方法中初始化
     */
    public LoadImage(){
        init();
    }

    /**
     * 初始化实例，创建线程池，启动线程，设置缓存
     */
    private void init(){
        HandlerThread taskThread = new HandlerThread("handlerThread");
        taskThread.start();
        mThreadHandler = new Handler(taskThread.getLooper()){
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
                return value.getByteCount();//返回一张图的大小
            }
        };
        mFixedThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        mTaskList = new LinkedList<Runnable>();
        semaphore = new Semaphore(THREAD_COUNT);//根据线程池中的线程数来创建信号量
    }

    /**
     * 从缓存中获取图片
     * @param key
     * @return
     */
    private Bitmap getFromLruCache(String key){
        return mLruCache.get(key);//根据KEY值从缓存中获取图片
    }

    /**
     * 将图片添加到缓存当中
     * @param key
     * @param bitmap
     */
    private void addToLruCache(String key , Bitmap bitmap){
        if (getFromLruCache(key) == null && bitmap !=null){
            mLruCache.put(key , bitmap);//将图片添加到缓存当中
        }
    }

    /**
     * 从任务队列中获取任务
     * @return
     */
    private Runnable getLoadTask(){
        try {
            semaphore.acquire();//获取信号量，获取不到则阻塞，保证线程池中有空闲线程才从任务中取出任务执行
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mTaskList.removeLast();//从任务队列的最后一个开始取
    }

    /**
     * 添加任务
     * @param loadTask
     */
    private void addLoadTask(Runnable loadTask){
        mTaskList.add(loadTask);
        mThreadHandler.sendEmptyMessage(TASK_COME);//发送消息通知执行任务
    }

    /**
     * 为每个Item设置图片
     * @param path
     * @param imageView
     */
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
                public void run() {//任务详情
                    Holder holder = new Holder();
                    Bitmap bm = CompressImage.compressImage(path , COMPRESS_SIZE , COMPRESS_SIZE);//压缩图片
                    addToLruCache(path , bm);
                    holder.bitmap = bm;
                    holder.imageView = imageView;
                    holder.path = path ;
                    Message message = Message.obtain();
                    message.obj = holder ;
                    mHandler.sendMessage(message);
                    semaphore.release();//释放信号量
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
