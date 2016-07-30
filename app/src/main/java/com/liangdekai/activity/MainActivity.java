package com.liangdekai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.GridView;
import android.widget.Toast;

import com.liangdekai.adapter.PhotoAdapter;
import com.liangdekai.photodepot.R;
import com.liangdekai.util.LoadImage;
import com.liangdekai.util.ScanFile;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{
    private static final int FAILED = 0;
    private static final int SUCCEED = 1;
    private ProgressDialog progressDialog ;
    private GridView mGridView ;
    private PhotoAdapter mAdapter;
    private LoadImage mLoadImage;
    private List<String> mImageList = new ArrayList<String>();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCEED :
                    closeDialog();
                    mAdapter = new PhotoAdapter(MainActivity.this , mImageList);
                    mGridView.setAdapter(mAdapter);
                    break;
                case FAILED :
                    closeDialog();
                    Toast.makeText(MainActivity.this , "无外部存储" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mLoadImage = LoadImage.getInstance();
        mGridView = (GridView) findViewById(R.id.main_gv_photo);
        getImage();
    }

    private void getImage(){
        showDialog();
        ScanFile.scanImageFile(this, new ScanFile.ScanListener() {
            @Override
            public void succeed(List<String> imageList) {
                mImageList = imageList;
                Message message = Message.obtain();
                message.what = SUCCEED ;
                mHandler.sendMessage(message);
            }

            @Override
            public void failed() {
                Message message = Message.obtain();
                message.what = FAILED ;
                mHandler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadImage.closeThreadPool();
    }

    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请耐心等待");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void closeDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
