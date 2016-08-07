package com.liangdekai.util;

/**
 * Created by asus on 2016/8/7.
 */
public class ImageTask implements Runnable {
    private TaskChain mTaskChain ;

    public ImageTask (TaskChain taskChain ){
        mTaskChain = taskChain ;
    }

    @Override
    public void run() {
        mTaskChain.handleTask();
    }
}
