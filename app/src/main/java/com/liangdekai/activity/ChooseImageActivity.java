package com.liangdekai.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.liangdekai.adapter.ChooseImageAdapter;
import com.liangdekai.photodepot.R;
import com.liangdekai.util.LoadImage;
import com.liangdekai.util.ScanFile;

import java.util.ArrayList;
import java.util.List;

public class ChooseImageActivity extends Activity implements View.OnClickListener{
    private static final int RESULT_OK = 1;
    private static final int FAILED = 0;
    private static final int SUCCEED = 1;
    private ProgressDialog progressDialog ;
    private GridView mGridView ;
    private Button mBtnBack ;
    private Button mBtnFinish ;
    private ChooseImageAdapter mAdapter;
    private LoadImage mLoadImage;
    private List<String> mImageList;//存储图片路径
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCEED :
                    closeDialog();
                    mAdapter = new ChooseImageAdapter(ChooseImageActivity.this , mImageList);
                    mGridView.setAdapter(mAdapter);
                    break;
                case FAILED :
                    closeDialog();
                    Toast.makeText(ChooseImageActivity.this , "无外部存储" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_image);
        initView();
        getImage();
        setClickListener();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        mLoadImage = LoadImage.getInstance();
        mImageList = new ArrayList<String>();
        mGridView = (GridView) findViewById(R.id.main_gv_photo);
        mBtnBack = (Button) findViewById(R.id.main_bt_back);
        mBtnFinish = (Button) findViewById(R.id.main_bt_finish);
    }

    /**
     * 扫描手机上的图片，将图片的路径数据回调
     */
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

    /**
     * 设置事件监听
     */
    private void setClickListener(){
        mBtnBack.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
    }

    /**
     * 关闭活动的时候，关闭线程池
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadImage.closeThreadPool();
    }

    /**
     *创建展示对话框
     */
    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("请耐心等待");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_bt_back :
                finish();
                break;
            case R.id.main_bt_finish :
                Intent intent = new Intent();
                intent.putStringArrayListExtra("selected" , (ArrayList<String>) mAdapter.getSelectedList());
                setResult(RESULT_OK, intent);//返回选择的图片路径数据到上一活动
                finish();
                break;
        }
    }
}
