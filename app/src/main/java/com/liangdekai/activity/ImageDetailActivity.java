package com.liangdekai.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.photodepot.R;
import com.liangdekai.util.CompressImage;

import java.util.ArrayList;
import java.util.List;

public class ImageDetailActivity extends Activity implements ViewPager.OnPageChangeListener{
    private ViewPager mViewPager ;
    private TextView mTextView ;
    private List<String> mImageList;
    private int mLevel ;

    public static void startActivity(Context context ,List<String> list , int position){
        Intent intent = new Intent(context , ImageDetailActivity.class);
        intent.putStringArrayListExtra("imageList" , (ArrayList<String>) list);
        intent.putExtra("level" , position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detail_image);
        init();
    }

    private void init(){
        mViewPager = (ViewPager) findViewById(R.id.activity_vp_detail);
        mTextView = (TextView) findViewById(R.id.activity_tv_record);
        mImageList = getIntent().getStringArrayListExtra("imageList");
        mLevel = getIntent().getIntExtra("level" , 0);
        setIndex(mLevel);
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mLevel);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setIndex(position);
    }

    private void setIndex(int position){
        int temp = position + 1 ;
        String current = ""+temp;
        String sum = ""+mImageList.size();
        String result = current+"/"+sum;
        mTextView.setText(result);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(ImageDetailActivity.this).inflate(R.layout.part_vp_show, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.detail_show);
            Bitmap bitmap = BitmapFactory.decodeFile(mImageList.get(position));
            if (bitmap.getByteCount() > 6*1024*1024){
                bitmap = CompressImage.compressImage(mImageList.get(position) , 500 , 500);
            }
            imageView.setImageBitmap(bitmap);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
