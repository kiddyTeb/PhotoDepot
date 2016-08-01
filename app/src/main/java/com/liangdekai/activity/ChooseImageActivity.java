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
    private List<String> mImageList;
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

    private void setClickListener(){
        mBtnBack.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
    }

    private void initView(){
        mLoadImage = LoadImage.getInstance();
        mImageList = new ArrayList<String>();
        mGridView = (GridView) findViewById(R.id.main_gv_photo);
        mBtnBack = (Button) findViewById(R.id.main_bt_back);
        mBtnFinish = (Button) findViewById(R.id.main_bt_finish);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_bt_back :
                finish();
                break;
            case R.id.main_bt_finish :
                Intent intent = new Intent();
                intent.putStringArrayListExtra("selected" , (ArrayList<String>) mAdapter.getSelectedList());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
