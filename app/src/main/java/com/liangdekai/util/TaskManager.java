package com.liangdekai.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载，设置缓存的工具类
 */
public class TaskManager {
    public static final int TASK_COME = 0;
    public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors() + 1 ;
    private static TaskManager mTaskManager;
    private Handler mThreadHandler;
    public volatile Semaphore semaphore ;
    //private LruCache<String , Bitmap> mLruCache ;
    private ExecutorService mFixedThreadPool;
    private LinkedList<Runnable> mTaskList ;

    /**
     * 获取该类的实例
     * @return
     */
    public static TaskManager getInstance(){
        if (mTaskManager == null){
            synchronized (TaskManager.class){
                if (mTaskManager == null){
                    mTaskManager = new TaskManager();
                }
            }
        }
        return mTaskManager;
    }

    /**
     * 构造方法中初始化
     */
    public TaskManager(){
        init();
    }

    /**
     * 初始化实例，创建线程池，启动线程
     */
    private void init(){
        HandlerThread taskThread = new HandlerThread("handlerThread");
        taskThread.start();
        mThreadHandler = new Handler(taskThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                if (!mFixedThreadPool.isShutdown()){
                    mFixedThreadPool.execute(getLoadTask());
                }
            }
        };

        /*int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        int cacheMemory = maxMemory / 6 ;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();//返回一张图的大小
            }
        };*/
        mFixedThreadPool = Executors.newFixedThreadPool(THREAD_COUNT);
        mTaskList = new LinkedList<Runnable>();
        semaphore = new Semaphore(THREAD_COUNT);//根据线程池中的线程数来创建信号量
    }

    /**
     * 从缓存中获取图片
     * @param key
     * @return
     */
    /*public Bitmap getFromLruCache(String key){
        return mLruCache.get(key);//根据KEY值从缓存中获取图片
    }

    /**
     * 将图片添加到缓存当中
     * @param key
     * @param bitmap
     */
    /*public void addToLruCache(String key , Bitmap bitmap){
        if (getFromLruCache(key) == null && bitmap !=null){
            mLruCache.put(key , bitmap);//将图片添加到缓存当中
        }
    }*/

    /**
     * 从任务队列中获取任务
     * @return
     */
    public Runnable getLoadTask(){
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
    public void addLoadTask(Runnable loadTask){
        mTaskList.add(loadTask);
        mThreadHandler.sendEmptyMessage(TASK_COME);//发送消息通知执行任务
    }
}
